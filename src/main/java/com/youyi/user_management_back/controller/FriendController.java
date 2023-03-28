package com.youyi.user_management_back.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.youyi.user_management_back.common.BaseResponse;
import com.youyi.user_management_back.common.ErrorCode;
import com.youyi.user_management_back.common.ResultUtils;
import com.youyi.user_management_back.exception.BusinessException;
import com.youyi.user_management_back.model.domain.User;
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
    @GetMapping
    public BaseResponse<List<FriendUserVO>> listApply(HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        return ResultUtils.success(friendService.listApply(loginUser));
    }

    private User nullJudgeAndGetLoginUser(Object selfRequest, HttpServletRequest request) {
        if (selfRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        return userService.getLoginUser(request);
    }


}
