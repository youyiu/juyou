package com.youyi.user_management_back.mapper;

import com.youyi.user_management_back.model.domain.Friend;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Update;

import java.util.List;
import java.util.Map;

/**
* @author chen
* @description 针对表【friend(好友)】的数据库操作Mapper
* @createDate 2023-03-25 21:45:04
* @Entity com.youyi.user_management_back.model.domain.Friend
*/
public interface FriendMapper extends BaseMapper<Friend> {

    int judgeFriend(Long userId,Long friendId);

    //根据用户id和好友id获取已经是好友关系的记录信息
    Long getFriendRecord(Long userId,Long friendId);
    boolean applyFriend(Map<String,Object> apply);

    @Update("update friend set status = #{status} where id = #{id}")
    boolean handleApply(long id,Integer status);

    List<Long> listMyFriend(long loginUserId);
}




