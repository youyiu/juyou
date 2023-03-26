package com.youyi.user_management_back.mapper;

import com.youyi.user_management_back.model.domain.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
* @author chen
* @description 针对表【user(用户)】的数据库操作Mapper
* @createDate 2022-09-21 21:54:02
* @Entity generator.domain.User
*/
@Mapper
public interface UserMapper extends BaseMapper<User> {

    @Select("select id from user where id = #{id}")
    boolean existUser(long id);
}




