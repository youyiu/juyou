package com.youyi.user_management_back.service;

import com.youyi.user_management_back.model.domain.Friend;
import com.baomidou.mybatisplus.extension.service.IService;
import com.youyi.user_management_back.model.domain.User;
import com.youyi.user_management_back.model.request.FriendAddRequest;

/**
* @author chen
* @description 针对表【friend(好友)】的数据库操作Service
* @createDate 2023-03-25 21:45:04
*/
public interface FriendService extends IService<Friend> {

    boolean addFriend(FriendAddRequest friend, User loginUser);
}
