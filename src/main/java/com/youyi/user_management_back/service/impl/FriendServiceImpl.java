package com.youyi.user_management_back.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.youyi.user_management_back.common.ErrorCode;
import com.youyi.user_management_back.constant.FriendConstant;
import com.youyi.user_management_back.exception.BusinessException;
import com.youyi.user_management_back.mapper.UserMapper;
import com.youyi.user_management_back.model.domain.Friend;
import com.youyi.user_management_back.model.domain.User;
import com.youyi.user_management_back.model.request.FriendAddRequest;
import com.youyi.user_management_back.model.request.FriendHandleRequest;
import com.youyi.user_management_back.service.FriendService;
import com.youyi.user_management_back.mapper.FriendMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;

/**
* @author chen
* @description 针对表【friend(好友)】的数据库操作Service实现
* @createDate 2023-03-25 21:45:04
*/
@Service
public class FriendServiceImpl extends ServiceImpl<FriendMapper, Friend>
    implements FriendService{

    @Resource
    private FriendMapper friendMapper;

    @Resource
    private UserMapper userMapper;

    @Override
    public boolean addFriend(FriendAddRequest friend, User loginUser) {
        if (friend == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long friendId = friend.getFriendId();
        long userId = loginUser.getId();
        if (friendId <= 0 || friendId == userId) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        //判断是否存在好友这个用户
        boolean exist = userMapper.existUser(friendId);
        if (!exist) {
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        // 1. 判断是否已经时是好友或已经发送了请求
        int result = friendMapper.judgeFriend(userId, friendId);
        // 1.1 是，退出
        if (result > 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"");
        }
        // 2. 校验信息
        // 2.1 申请原因是否大于512
        String reason = friend.getReason();
        if (StringUtils.isNotBlank(reason) && reason.length() > 512) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"申请原因不符合要求");
        }
        // 3. 发送请求
        HashMap<String, Object> apply = new HashMap<>();
        apply.put("userId",userId);
        apply.put("friendId",friendId);
        apply.put("reason",StringUtils.defaultIfBlank(reason, "无"));
        return friendMapper.applyFriend(apply);
    }

    @Override
    public boolean handleApply(FriendHandleRequest handleRequest, User loginUser) {
        long id = handleRequest.getId();
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Friend friendApply = getById(id);
        if (friendApply == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        //1. 判断请求是否是添加的自己
        Long friendId = friendApply.getFriendId();
        if(friendId != loginUser.getId()) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
        //2. 判断申请状态
        //2.1 已拒绝/同意，返回
        Integer status = friendApply.getStatus();
        if (status == FriendConstant.ACCEPTED_STATUS || status == FriendConstant.REJECTED_STATUS) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
        //3. 判断用户接受请求还是拒绝请求
        Integer requestStatus = handleRequest.getStatus();
        if (requestStatus != FriendConstant.REJECTED_STATUS &&
                requestStatus != FriendConstant.ACCEPTED_STATUS) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        //接受请求/拒绝请求
        return friendMapper.handleApply(id,requestStatus);
    }


}




