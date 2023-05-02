package com.youyi.user_management_back.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youyi.user_management_back.common.BaseResponse;
import com.youyi.user_management_back.common.ErrorCode;
import com.youyi.user_management_back.common.ResultUtils;
import com.youyi.user_management_back.exception.BusinessException;
import com.youyi.user_management_back.model.domain.User;
import com.youyi.user_management_back.model.request.UserLoginRequest;
import com.youyi.user_management_back.model.request.UserRegisterRequest;
import com.youyi.user_management_back.service.UserService;
import com.youyi.user_management_back.utils.ConcurrentUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;

import static com.youyi.user_management_back.constant.UserConstant.USER_LOGIN_STATE;

/**用户控制器
*/
@CrossOrigin
@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    @Resource
    private UserService userService;

    @Resource
    private RedisTemplate<String,Object> redisTemplate;

    //用户注册
    @PostMapping("/register")
    public BaseResponse<Long> userRegister(@RequestBody UserRegisterRequest userRegisterRequest){
        if (userRegisterRequest == null){
            //return null;
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();
        if (StringUtils.isAnyBlank(userAccount,userPassword,checkPassword)){
            return null;
        }
        long result = userService.userRegister(userAccount, userPassword, checkPassword);
        return ResultUtils.success(result);
    }

    //用户登录
    @PostMapping("/login")
    public BaseResponse<User> userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request){
        if (userLoginRequest == null){
            return null;
        }
        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();
        if (StringUtils.isAnyBlank(userAccount,userPassword)){
            return null;
        }
        User user = userService.userLogin(userAccount, userPassword, request);
        return ResultUtils.success(user);
    }

    //用户注销
    @PostMapping("/logout")
    public BaseResponse<Integer> userLogout(HttpServletRequest request){
        if (request == null){
            return null;
        }
        int result = userService.userLogout(request);
        return ResultUtils.success(result);
    }

    //获取当前用户登录态
    @GetMapping("current")
    public BaseResponse<User> getCurrentUser(HttpServletRequest request){
        User currentUser = (User)request.getSession().getAttribute(USER_LOGIN_STATE);
        if (currentUser == null){
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        Long userID = currentUser.getId();
        User user = userService.getById(userID);
        User safetyUser = userService.getSafetyUser(user);
        return ResultUtils.success(safetyUser);
    }

    private ExecutorService executorService =
            new ThreadPoolExecutor(40,1000,10000,
                    TimeUnit.MINUTES,new ArrayBlockingQueue<>(1000));

    //查询用户
    @GetMapping("/search")
    public BaseResponse<List<User>> search(String username,HttpServletRequest request) throws ExecutionException, InterruptedException {
        //仅管理员可以查询
        if(!userService.isAdmin(request)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
//        List<User> list = userService.list();
        if (StringUtils.isBlank(username)){
            return ResultUtils.success(null);
        }
        Future<List<User>> listExactFuture = ConcurrentUtils.doJob(executorService, () -> {
            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(User::getUserAccount, username);
            return userService.list(queryWrapper);
        });
        Future<List<User>> listLikeFuture = ConcurrentUtils.doJob(executorService, () -> {
            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.like(User::getUserAccount, username).or().like(User::getUsername,username);
            return userService.list(queryWrapper);
        });
        List<User> userList = listExactFuture.get();
        if (!CollectionUtils.isEmpty(userList)) {
            listLikeFuture.cancel(true);
            return ResultUtils.success(userList
                    .stream().map(user -> userService.getSafetyUser(user)).collect(Collectors.toList()));
        }
        return ResultUtils.success(listLikeFuture.get()
                .stream().map(user -> userService.getSafetyUser(user)).collect(Collectors.toList()));

//        Callable<User> userCallable = () -> null;
//        Future<User> future = executorService.submit(userCallable);
//        User user = future.get();
//        queryWrapper.like(User::getUsername,username);
//        return ResultUtils.success(null);

//        List<User> list1 = userList.stream().map(user -> userService.getSafetyUser(user)).collect(Collectors.toList());
//        return ResultUtils.success(list1);
    }

    //标签相近用户推荐
    @GetMapping("/recommend")
    public BaseResponse<Page<User>> RecommendUsers(long pageSize, long pageNum, HttpServletRequest request){
        User loginUser = userService.getLoginUser(request);
        //判断缓存中是否有数据
        // 1.如果有缓存，直接读缓存
        String redisKey = String.format("youpao:user:recommend:%s", loginUser.getId());
        Page<User> userPage = (Page<User>) redisTemplate.opsForValue().get(redisKey);
        if (userPage != null) {
            return ResultUtils.success(userPage);
        }
        // 2.1无缓存，查数据库
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        userPage = userService
                .page(new Page<>(pageNum,pageSize),queryWrapper);
        // 2.2 写入缓存
        try {
            redisTemplate.opsForValue().set(redisKey,userPage,10000, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            log.error("redis set key error",e);
        }
        return ResultUtils.success(userPage);
    }

    //修改用户信息
    @PostMapping("/update")
    public BaseResponse<Integer> updateUser(@RequestBody User user, HttpServletRequest request) {
        // 校验参数是否为空
        if (user == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        int result = userService.updateUser(user,loginUser);
        return ResultUtils.success(result);
    }


    //删除用户
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteUser(@RequestBody long id,HttpServletRequest request){
        //仅管理员可以查询
        if(!userService.isAdmin(request)){
            return null;
        }
        if (id < 0){
            return null;
        }
        boolean b = userService.removeById(id);
        return ResultUtils.success(b);
    }

    @GetMapping("/search/tags")
    public BaseResponse<List<User>> searchUserTags(@RequestParam(required = false) List<String> tagNameList) {
        if (CollectionUtils.isEmpty(tagNameList)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        return ResultUtils.success(userService.searchUserByTags(tagNameList));
    }

    /**
     * 获取最匹配的队伍
     * @param num
     * @param request
     * @return
     */
    @GetMapping("/match")
    public BaseResponse<List<User>> matchUsers(long num, HttpServletRequest request){
        if (num < 0 || num > 20) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        return ResultUtils.success(userService.matchUsers(num,loginUser));
    }

}
