package com.youyi.user_management_back.service;

import com.youyi.user_management_back.model.domain.Friend;
import com.baomidou.mybatisplus.extension.service.IService;
import com.youyi.user_management_back.model.domain.User;
import com.youyi.user_management_back.model.request.FriendAddRequest;
import com.youyi.user_management_back.model.request.FriendHandleRequest;
import com.youyi.user_management_back.model.vo.FriendUserVO;

import java.util.List;

/**
* @author chen
* @description 针对表【friend(好友)】的数据库操作Service
* @createDate 2023-03-25 21:45:04
*/
public interface FriendService extends IService<Friend> {

    boolean addFriend(FriendAddRequest friend, User loginUser);

    boolean handleApply(FriendHandleRequest handleRequest, User loginUser);

    List<User> listMyFriend(User loginUser);

    List<FriendUserVO> listApply(User loginUser);
}
