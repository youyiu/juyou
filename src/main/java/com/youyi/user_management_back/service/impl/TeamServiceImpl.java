package com.youyi.user_management_back.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.youyi.user_management_back.common.ErrorCode;
import com.youyi.user_management_back.exception.BusinessException;
import com.youyi.user_management_back.mapper.TeamMapper;
import com.youyi.user_management_back.model.domain.Team;
import com.youyi.user_management_back.model.domain.TeamNotification;
import com.youyi.user_management_back.model.domain.User;
import com.youyi.user_management_back.model.domain.UserTeam;
import com.youyi.user_management_back.model.dto.TeamQuery;
import com.youyi.user_management_back.model.enums.TeamStatusEnum;
import com.youyi.user_management_back.model.request.TeamJoinRequest;
import com.youyi.user_management_back.model.request.TeamQuitRequest;
import com.youyi.user_management_back.model.request.TeamUpdateRequest;
import com.youyi.user_management_back.model.vo.TeamUserVO;
import com.youyi.user_management_back.model.vo.UserVO;
import com.youyi.user_management_back.service.TeamService;
import com.youyi.user_management_back.service.UserService;
import com.youyi.user_management_back.service.UserTeamService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * @author chen
 * @description 针对表【team(队伍)】的数据库操作Service实现
 * @createDate 2023-03-07 19:13:01
 */
@Service
public class TeamServiceImpl extends ServiceImpl<TeamMapper, Team>
        implements TeamService {

    @Resource
    private UserTeamService userTeamService;

    @Resource
    private UserService userService;

    @Resource
    private RedissonClient redissonClient;

    @Resource
    private RabbitTemplate rabbitTemplate;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public long addTeam(Team team, User loginUser) {
        // 1.请求参数是否为空
        if (team == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 2.是否登录，未登录不允许创建
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        long userId = loginUser.getId();
        // 3.校验信息
        //  3.1 队伍人数 > 1 且 <= 20
        int maxNum = Optional.ofNullable(team.getMaxNum()).orElse(0);
        if (maxNum < 1 || maxNum >= 20) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍人数不满足要求");
        }
        //  3.2 队伍标题 <= 20
        String name = team.getName();
        if (StringUtils.isBlank(name) || name.length() > 20) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍标题不满足要求");
        }
        //  3.3 描述 <= 512
        String description = team.getDescription();
        if (StringUtils.isNotBlank(description) && description.length() > 512) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍描述不满足要求");
        }
        //  3.4 status 是否公开
        Integer status = Optional.ofNullable(loginUser.getUserStatus()).orElse(0);
        TeamStatusEnum statusEnum = TeamStatusEnum.getEnumByValue(status);
        if (statusEnum == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍状态不满足要求");
        }
        //  3.5 如果status是加密状态，一定要有密码，且密码 <= 20
        String password = team.getPassword();
        if (TeamStatusEnum.SECRET.equals(statusEnum) && (StringUtils.isBlank(password) || password.length() > 32)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍密码不满足要求");
        }
        // 3.6 超时时间 > 当前时间
        Date expireTime = team.getExpireTime();
        if (new Date().after(expireTime)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "超时时间 > 当前时间");
        }
        //  3.7 校验用户最多创建5个队伍
        QueryWrapper<Team> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId", userId);
        long hasTeamNum = this.count(queryWrapper);
        // TODO 高并发情况下，利用锁解决创建队伍超过5个问题
        if (hasTeamNum >= 5) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户最多创建5个队伍");
        }
        //  4.插入队伍信息到队伍表
        team.setId(null);
        team.setUserId(userId);
        boolean result = this.save(team);
        Long teamId = team.getId();
        if (!result || teamId == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "创建队伍失败");
        }
        //  5.插入用户 => 队伍关系到表
        UserTeam userTeam = new UserTeam();
        userTeam.setUserId(userId);
        userTeam.setTeamId(teamId);
        userTeam.setJoinTime(new Date());
        result = userTeamService.save(userTeam);
        if (!result) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "创建队伍失败");
        }
        return teamId;
    }

    @Override
    public List<TeamUserVO> listTeams(TeamQuery teamQuery, boolean isAdmin) {
        if (teamQuery == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        QueryWrapper<Team> queryWrapper = new QueryWrapper<>();
        Long id = teamQuery.getId();
        if (id != null && id > 0) {
            queryWrapper.eq("id", id);
        }
        List<Long> idList = teamQuery.getIdList();
        if (CollectionUtils.isNotEmpty(idList)) {
            queryWrapper.in("id",idList);
        }
        String searchText = teamQuery.getSearchText();
        if (StringUtils.isNotBlank(searchText)) {
            queryWrapper.and(qw -> qw.like("name", searchText).or().like("description", searchText));
        }
        String name = teamQuery.getName();
        if (StringUtils.isNotBlank(name)) {
            queryWrapper.like("name", name);
        }
        String description = teamQuery.getDescription();
        if (StringUtils.isNotBlank(description)) {
            queryWrapper.like("description", description);
        }
        Integer maxNum = teamQuery.getMaxNum();
        if (maxNum != null && maxNum > 0) {
            queryWrapper.eq("maxNum", maxNum);
        }
        Long userId = teamQuery.getUserId();
        if (userId != null && userId > 0) {
            queryWrapper.eq("userId", userId);
        }
        Integer status = teamQuery.getStatus();
        TeamStatusEnum statusEnum = TeamStatusEnum.getEnumByValue(status);
        if (statusEnum == null) {
            statusEnum = TeamStatusEnum.PUBLIC;
        }
        if (!isAdmin && statusEnum.equals(TeamStatusEnum.PRIVATE)) {
            throw new BusinessException(ErrorCode.NO_AUTH);
        }
        queryWrapper.eq("status", statusEnum.getValue());
        // 不展示已过期的队伍
        // expireTime is null or expireTime > now()
        queryWrapper.and(qw -> qw.gt("expireTime", new Date()).or().isNull("expireTime"));
        List<Team> teamList = this.list(queryWrapper);
        if (CollectionUtils.isEmpty(teamList)) {
            return new ArrayList<>();
        }
        //关联查询用户信息
        // 1. 自写SQL   2. 关联查询
//        查询队伍和创建人信息
//        查询队伍和已加入队伍成员信息
        List<TeamUserVO> teamUserVOS = new ArrayList<>();
        for (Team team : teamList) {
            userId = team.getUserId();
            if (userId == null) {
                continue;
            }
            User user = userService.getById(userId);
            TeamUserVO teamUserVO = new TeamUserVO();
            BeanUtils.copyProperties(team, teamUserVO);

            if (user != null) {
                UserVO userVO = new UserVO();
                BeanUtils.copyProperties(user, userVO);
                teamUserVO.setCreateUser(userVO);
            }
            teamUserVOS.add(teamUserVO);
        }
        return teamUserVOS;
    }

    // TODO 加密状态时修改密码，如果密码为空，将状态转为公开
    @Override
    public boolean updateTeam(TeamUpdateRequest teamUpdateRequest,User loginUser) {
        if (teamUpdateRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Long id = teamUpdateRequest.getId();
        Team oldTeam = getTeamById(id);
        //只有管理员和本人可以修改
        if (oldTeam.getUserId() != loginUser.getId() && !userService.isAdmin(loginUser)) {
            throw new BusinessException(ErrorCode.NO_AUTH);
        }
        TeamStatusEnum statusEnum = TeamStatusEnum.getEnumByValue(teamUpdateRequest.getStatus());
        if (TeamStatusEnum.SECRET.equals(statusEnum)) {
            if (!StringUtils.isBlank(teamUpdateRequest.getPassword())) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR,"加密房间必须设置密码");
            }
        }
        Team updateTeam = new Team();
        BeanUtils.copyProperties(teamUpdateRequest,updateTeam);
        return this.updateById(updateTeam);
    }

    @Override
    public boolean joinTeam(TeamJoinRequest teamJoinRequest,User loginUser) {
        if (teamJoinRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long id = loginUser.getId();

        Long teamId = teamJoinRequest.getTeamId();
        Team team = getTeamById(teamId);
        Date expireTime = team.getExpireTime();
        if (expireTime != null && expireTime.before(new Date())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"队伍已过期");
        }
        Integer status = team.getStatus();
        TeamStatusEnum statusEnum = TeamStatusEnum.getEnumByValue(status);
        if (TeamStatusEnum.PRIVATE.equals(statusEnum)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"禁止加入私有队伍");
        }
        String password = teamJoinRequest.getPassword();
        if (TeamStatusEnum.SECRET.equals(statusEnum)) {
            if (StringUtils.isBlank(password) || !password.equals(team.getPassword())) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR,"密码错误");
            }
        }

        //分布式锁
        RLock lock = redissonClient.getLock("jiaoyou:join_team");
        try {
            //只有一个线程
            if (lock.tryLock(0,30000, TimeUnit.MICROSECONDS)) {
                // 该用户已经加入队伍的数量
                QueryWrapper<UserTeam> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("userId",id);
                long hasJoinNum = userTeamService.count(queryWrapper);
                if (hasJoinNum > 5) {
                    throw new BusinessException(ErrorCode.PARAMS_ERROR,"最多加入5个队伍");
                }

                //不能重复加入以加入的队伍
                queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("teamId",teamId);
                queryWrapper.eq("userId",id);
                long hasUserJoinTeam = userTeamService.count(queryWrapper);
                if (hasUserJoinTeam > 0) {
                    throw new BusinessException(ErrorCode.PARAMS_ERROR,"已加入该队伍");
                }

                // 已经加入该队伍的人数
                long teamHasJoinNum = getTeamUserByTeamId(teamId);
                if (teamHasJoinNum >= team.getMaxNum()) {
                    throw new BusinessException(ErrorCode.PARAMS_ERROR,"队伍人数已满");
                }

                //修改队伍信息
                UserTeam userTeam = new UserTeam();
                userTeam.setUserId(id);
                userTeam.setTeamId(teamId);
                userTeam.setJoinTime(new Date());
                return userTeamService.save(userTeam);
            }
        } catch (InterruptedException e) {
            log.error("tryLock error",e);
        }finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
        return false;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean quitTeam(TeamQuitRequest teamQuitRequest, User loginUser) {
        if (teamQuitRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Long teamId = teamQuitRequest.getTeamId();
        Team team = getTeamById(teamId);
        long userId = loginUser.getId();
        UserTeam userTeam = new UserTeam();
        userTeam.setUserId(userId);
        userTeam.setTeamId(teamId);
        QueryWrapper<UserTeam> queryWrapper = new QueryWrapper<>(userTeam);
        long count = userTeamService.count(queryWrapper);
        if(count == 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"未加入队伍");
        }
        long teamHasJoinNum = getTeamUserByTeamId(teamId);
        //创建消息
        TeamNotification ntf = null;
        if (teamHasJoinNum == 1) {
            // 删除队伍和队伍关系
            removeById(teamId);
//            queryWrapper = new QueryWrapper<>();
//            queryWrapper.eq("teamId",teamId);
//            return userTeamService.remove(queryWrapper);
        }else {
            ntf = new TeamNotification();
            ntf.setStatus(0);
            ntf.setOperateUserId(userId);
            ntf.setAcceptUserId(team.getUserId());
            //是否为队长
            if (team.getUserId() == userId) {
                ntf.setStatus(1);
                // 把队伍转移给最早加入的队伍
                // 1. 查询已加入队伍的所有用户和加入时间
                queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("teamId",teamId);
                queryWrapper.last("order by id asc limit 2");
                List<UserTeam> userTeamList = userTeamService.list(queryWrapper);
                if (CollectionUtils.isEmpty(userTeamList) || userTeamList.size() <= 1) {
                    throw new BusinessException(ErrorCode.SYSTEM_ERROR);
                }
                UserTeam nextTeamUser = userTeamList.get(1);
                Long nextTeamLeaderId = nextTeamUser.getUserId();
                // 更新当前队伍的队长
                Team updateTeam = new Team();
                updateTeam.setId(teamId);
                updateTeam.setUserId(nextTeamLeaderId);
                boolean result = this.updateById(team);
                if (!result) {
                    throw new BusinessException(ErrorCode.SYSTEM_ERROR,"更新队长失败");
                }
                ntf.setAcceptUserId(nextTeamLeaderId);
                queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("teamId",teamId);
                queryWrapper.eq("userId",userId);
            }
        }
        // 移除关联
        boolean remove = userTeamService.remove(queryWrapper);
        if (remove && ntf != null) {
            rabbitTemplate.convertAndSend("team.direct","quit",ntf);
        }
        return remove;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteTeam(Long teamId,User loginUser) {
        //校验队伍是否存在
        Team team = this.getTeamById(teamId);
        //检验是否为队长
        if (loginUser.getId() != team.getUserId()) {
            throw new BusinessException(ErrorCode.NO_AUTH,"无访问权限");
        }
        // 移除所有加入队伍的关联信息
        QueryWrapper<UserTeam> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("teamId",teamId);
        boolean remove = userTeamService.remove(queryWrapper);
        if (!remove) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"删除队伍失败");
        }
        //删除队伍
        return this.removeById(teamId);
    }


    @NotNull
    private Team getTeamById(Long teamId) {
        if (teamId == null || teamId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Team team = this.getById(teamId);
        if (team == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR,"队伍不存在");
        }
        return team;
    }

    /**
     * 获取某队伍当前人数
     * @param teamId
     * @return
     */
    private long getTeamUserByTeamId(long teamId) {
        QueryWrapper<UserTeam> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("teamId",teamId);
        return userTeamService.count(queryWrapper);
    }
}




