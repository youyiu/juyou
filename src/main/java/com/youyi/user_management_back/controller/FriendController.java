package com.youyi.user_management_back.controller;

import com.youyi.user_management_back.common.BaseResponse;
import com.youyi.user_management_back.common.ErrorCode;
import com.youyi.user_management_back.common.ResultUtils;
import com.youyi.user_management_back.exception.BusinessException;
import com.youyi.user_management_back.model.domain.User;
import com.youyi.user_management_back.model.request.FriendAddRequest;
import com.youyi.user_management_back.model.request.FriendHandleRequest;
import com.youyi.user_management_back.service.FriendService;
import com.youyi.user_management_back.service.UserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/friend")
public class FriendController {

    @Resource
    private FriendService friendService;

    @Resource
    private UserService userService;

    @PostMapping("/add")
    public BaseResponse<Boolean> addFriend(@RequestBody FriendAddRequest addRequest, HttpServletRequest request) {
        User loginUser = nullJudgeAndGetLoginUser(addRequest, request);
        boolean result = friendService.addFriend(addRequest,loginUser);
        if (!result) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"添加失败");
        }
        return ResultUtils.success(true);
    }


    @PostMapping("/handle")
    public BaseResponse<Boolean> handleApply(@RequestBody FriendHandleRequest handleRequest,HttpServletRequest request) {
        User loginUser = nullJudgeAndGetLoginUser(handleRequest, request);
        boolean result = friendService.handleApply(handleRequest,loginUser);
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
