package com.youyi.user_management_back.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.youyi.user_management_back.model.domain.Team;
import com.youyi.user_management_back.model.domain.User;
import com.youyi.user_management_back.model.dto.TeamQuery;
import com.youyi.user_management_back.model.request.TeamJoinRequest;
import com.youyi.user_management_back.model.request.TeamQuitRequest;
import com.youyi.user_management_back.model.request.TeamUpdateRequest;
import com.youyi.user_management_back.model.vo.TeamUserVO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
* @author chen
* @description 针对表【team(队伍)】的数据库操作Service
* @createDate 2023-03-07 19:13:01
*/
public interface TeamService extends IService<Team> {
    /**
     * 创建队伍
     * @param team
     * @param loginUser
     * @return
     */
    long addTeam(Team team, User loginUser);

    List<TeamUserVO> listTeams(TeamQuery teamQuery,boolean isAdmin);


    boolean updateTeam(TeamUpdateRequest teamUpdateRequest,User loginUser);

    boolean joinTeam(TeamJoinRequest teamJoinRequest,User loginUser);

    boolean quitTeam(TeamQuitRequest teamQuitRequest, User loginUser);

    boolean deleteTeam(Long teamId, User loginUser);
}
