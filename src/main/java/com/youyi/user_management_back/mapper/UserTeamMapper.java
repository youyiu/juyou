package com.youyi.user_management_back.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.youyi.user_management_back.model.domain.UserTeam;
import org.apache.ibatis.annotations.Mapper;

/**
* @author chen
* @description 针对表【user_team(用户队伍关系)】的数据库操作Mapper
* @createDate 2023-03-07 19:13:06
* @Entity com.youyi.user_management_back.model.domain.UserTeam
*/
@Mapper
public interface UserTeamMapper extends BaseMapper<UserTeam> {

}




