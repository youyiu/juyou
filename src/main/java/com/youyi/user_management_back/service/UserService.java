package com.youyi.user_management_back.service;

import com.youyi.user_management_back.common.BaseResponse;
import com.youyi.user_management_back.model.domain.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.youyi.user_management_back.model.vo.UserVO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;


/**
* @author chen
* @description 针对表【user(用户)】的数据库操作Service
* @createDate 2022-09-21 21:54:02
*/
public interface UserService extends IService<User> {



    /**
     *
     * @param userAccount 用户账户
     * @param userPassword 用户密码
     * @param checkPassword 用户校验密码
     * @return 新用户id
     */
    long userRegister(String userAccount,String userPassword,String checkPassword);

    User userLogin(String userAccount, String userPassword, HttpServletRequest request);

    User getSafetyUser(User originUser);
    //用户脱敏

    //用户注销
    int userLogout(HttpServletRequest request);

    /**
     * 根据标签搜索用户
     *
     * @param tagList
     * @return
     */
    List<User> searchUserByTags(List<String> tagList);

    int updateUser(User user, User loginUser);

    User getLoginUser(HttpServletRequest httpRequest);

    /*是否为管理员
     * */
    boolean isAdmin(HttpServletRequest request);

    /*是否为管理员
     * */
    boolean isAdmin(User loginUser);

    /**
     * 匹配用户
     * @param num
     * @param loginUser
     * @return
     */
    List<User> matchUsers(long num, User loginUser);
}
