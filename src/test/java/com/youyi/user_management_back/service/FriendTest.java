package com.youyi.user_management_back.service;

import com.youyi.user_management_back.mapper.FriendMapper;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
public class FriendTest {

    @Resource
    private FriendMapper friendMapper;

    @Test
    void testJudge() {
        int result = friendMapper.judgeFriend(1L, 2L);
        System.out.println(result);
    }
}
