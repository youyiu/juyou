package com.youyi.user_management_back.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.youyi.user_management_back.model.domain.Team;
import org.apache.ibatis.annotations.Mapper;

/**
* @author chen
* @description 针对表【team(队伍)】的数据库操作Mapper
* @createDate 2023-03-07 19:13:01
* @Entity com.youyi.user_management_back.model.domain.Team
*/
@Mapper
public interface TeamMapper extends BaseMapper<Team> {

}




