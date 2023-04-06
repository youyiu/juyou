package com.youyi.user_management_back.controller;

import com.youyi.user_management_back.common.BaseResponse;
import com.youyi.user_management_back.common.ErrorCode;
import com.youyi.user_management_back.common.ResultUtils;
import com.youyi.user_management_back.exception.BusinessException;
import com.youyi.user_management_back.model.domain.User;
import com.youyi.user_management_back.model.request.DeleteRequest;
import com.youyi.user_management_back.model.request.FriendAddRequest;
import com.youyi.user_management_back.model.request.FriendHandleRequest;
import com.youyi.user_management_back.model.vo.FriendUserVO;
import com.youyi.user_management_back.service.FriendService;
import com.youyi.user_management_back.service.UserService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/friend")
public class FriendController {

    @Resource
    private FriendService friendService;

    @Resource
    private UserService userService;

    /**
     * 发送添加好友请求
     * @param addRequest 请求信息：1.好友id，2.申请原因
     * @param request 获取登录用户信息
     * @return 是否发送成功
     */
    @PostMapping("/add")
    public BaseResponse<Boolean> addFriend(@RequestBody FriendAddRequest addRequest, HttpServletRequest request) {
        User loginUser = nullJudgeAndGetLoginUser(addRequest, request);
        boolean result = friendService.addFriend(addRequest,loginUser);
        if (!result) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"添加失败");
        }
        return ResultUtils.success(true);
    }

    /**
     * 处理添加请求
     * @param handleRequest 请求参数：1.请求id，2.处理方式
     * @param request 获取登录用户信息
     * @return 是否处理成功
     */
    @PostMapping("/handle")
    public BaseResponse<Boolean> handleApply(@RequestBody FriendHandleRequest handleRequest,HttpServletRequest request) {
        User loginUser = nullJudgeAndGetLoginUser(handleRequest, request);
        boolean result = friendService.handleApply(handleRequest,loginUser);
        if (!result) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
        return ResultUtils.success(true);
    }

    /**
     * 查看我的好友列表
     * @param request 获取登录用户信息
     * @return 好友信息
     */
    //TODO 返回的用户信息进行单独封装，只能查看到好友的部分信息
    @GetMapping("/list/myFriend")
    public BaseResponse<List<User>> listMyFriend(HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        return ResultUtils.success(friendService.listMyFriend(loginUser));
    }

    /**
     * 查看添加我为好友的申请列表
     * @param request 获取登录用户信息
     * @return 好友信息及申请状态
     */
    @GetMapping("/list/apply")
    public BaseResponse<List<FriendUserVO>> listApply(HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        return ResultUtils.success(friendService.listApply(loginUser));
    }

    /**
     * 根据名称查询我的好友
     * @param username 输入要查询的名称
     * @param request 获取登录用户信息
     * @return 查询到的好友信息
     */
    @GetMapping("/search")
    public BaseResponse<List<User>> search(String username,HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        List<User> userList = friendService.search(username,loginUser);
        return ResultUtils.success(userList);
    }

    /**
     * 删除好友
     * @param deleteRequest 要删除好友的id
     * @param request 获取登录用户信息
     * @return 是否删除成功
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> delete(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() < 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long userId = deleteRequest.getId();
        User loginUser = userService.getLoginUser(request);
        boolean result = friendService.deleteFriend(userId,loginUser);
        if (!result) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
        return ResultUtils.success(true);
    }

    private User nullJudgeAndGetLoginUser(Object selfRequest, HttpServletRequest request) {
        if (selfRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        return userService.getLoginUser(request);
    }


}
