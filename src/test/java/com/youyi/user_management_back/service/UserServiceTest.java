package com.youyi.user_management_back.service;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import com.youyi.user_management_back.model.domain.User;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
class UserServiceTest {

    @Resource
    private UserService userService;

    @Test
    public void testAddUser(){
        User user = new User();
        user.setUsername("test");
        user.setUserAccount("123");
        user.setAvatarUrl("https://www.woyaogexing.com/touxiang/qinglv/2022/1272734.html");
        user.setGender(0);
        user.setUserPassword("XXX");
        user.setPhone("123");
        user.setEmail("456");
        boolean save = userService.save(user);
        System.out.println(user.getId());
        Assertions.assertTrue(save);
    }

    @Test
    void userRegister() {
        String useAccount = "yupi";
        String password = "";
        String checkPassword = "123456";
        long l = userService.userRegister(useAccount, password, checkPassword);
        Assertions.assertEquals(-1,l);

        useAccount = "yu";
        l = userService.userRegister(useAccount, password, checkPassword);
        Assertions.assertEquals(-1,l);

        useAccount = "yupi";
        password = "123456";
        l = userService.userRegister(useAccount, password, checkPassword);
        Assertions.assertEquals(-1,l);

        useAccount = "yu pi";
        password = "12345678";
        l = userService.userRegister(useAccount, password, checkPassword);
        Assertions.assertEquals(-1,l);

        useAccount = "yu pi";
        checkPassword = "123456789";
        l = userService.userRegister(useAccount, password, checkPassword);
        Assertions.assertEquals(-1,l);

        useAccount = "123";
        checkPassword = "12345678";
        l = userService.userRegister(useAccount, password, checkPassword);
        Assertions.assertEquals(-1,l);

        useAccount = "youyi";
        l = userService.userRegister(useAccount, password, checkPassword);
        Assertions.assertTrue(l > 0);
    }

    @Test
    void userRegistertest() {
        String useAccount = "yupi";
        String password = "12345678";
        String checkPassword = "12345678";
        long l = userService.userRegister(useAccount, password, checkPassword);
        Assertions.assertTrue(l > 0);
    }

    @Test
    public void setUserServiceByTags() {
        List<String> tagListName = Arrays.asList("java","python");
        List<User> userList = userService.searchUserByTags(tagListName);;
        Assertions.assertNotNull(userList);
    }
}