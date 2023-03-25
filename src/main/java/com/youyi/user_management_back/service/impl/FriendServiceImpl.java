package com.youyi.user_management_back.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.youyi.user_management_back.model.domain.Friend;
import com.youyi.user_management_back.service.FriendService;
import com.youyi.user_management_back.mapper.FriendMapper;
import org.springframework.stereotype.Service;

/**
* @author chen
* @description 针对表【friend(好友)】的数据库操作Service实现
* @createDate 2023-03-25 21:45:04
*/
@Service
public class FriendServiceImpl extends ServiceImpl<FriendMapper, Friend>
    implements FriendService{

}




