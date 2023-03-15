package com.youyi.user_management_back.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.youyi.user_management_back.mapper.UserTeamMapper;
import com.youyi.user_management_back.model.domain.UserTeam;
import com.youyi.user_management_back.service.UserTeamService;
import org.springframework.stereotype.Service;

/**
* @author chen
* @description 针对表【user_team(用户队伍关系)】的数据库操作Service实现
* @createDate 2023-03-07 19:13:06
*/
@Service
public class UserTeamServiceImpl extends ServiceImpl<UserTeamMapper, UserTeam>
    implements UserTeamService {

}




