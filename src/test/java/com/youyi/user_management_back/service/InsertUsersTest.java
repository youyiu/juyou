package com.youyi.user_management_back.service;

import com.youyi.user_management_back.model.domain.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.StopWatch;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class InsertUsersTest {

    @Resource
    private UserService userService;

    private ExecutorService executorService =
            new ThreadPoolExecutor(40,1000,10000,
                    TimeUnit.MINUTES,new ArrayBlockingQueue<>(1000));

    @Test
    public void doInsertUsers() {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        final int INSERT_NUM = 100000;
        List<User> userList = new ArrayList<>();
        for (int i = 0; i< INSERT_NUM ;i ++ ) {
            User user = new User();
            user.setUsername("测试数据");
            user.setUserAccount("youyi");
            user.setAvatarUrl("https://thirdwx.qlogo.cn/mmopen/vi_32/yYiceiaZZfa0xRHFE67FNHRFsFBWGicVUIuC8VMBACz7cC7vKqEq2ibLr5rzuMmPMoztwThpL0BOnNiceDxHzzSWVzw/132");
            user.setGender(0);
            user.setUserPassword("12345678");
            user.setPhone("123");
            user.setEmail("123@qq.com");
            user.setTags("[]");
            user.setUserStatus(0);
            user.setUserRole(0);
            userList.add(user);
        }
        //20s 10万条
        userService.saveBatch(userList,1000);
        stopWatch.stop();
        System.out.println(stopWatch.getTotalTimeMillis());

    }

    @Test
    public void doConcurrencyInsertUsers() {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        //分10组
        int batchSize = 5000;
        int j = 0;
        List<CompletableFuture<Void>> futureList = new ArrayList<>();
        for (int i = 0; i< 20 ;i ++ ) {
            List<User> userList = new ArrayList<>();
            while (true) {
                j++;
                User user = new User();
                user.setUsername("测试数据");
                user.setUserAccount("youyi");
                user.setAvatarUrl("https://thirdwx.qlogo.cn/mmopen/vi_32/yYiceiaZZfa0xRHFE67FNHRFsFBWGicVUIuC8VMBACz7cC7vKqEq2ibLr5rzuMmPMoztwThpL0BOnNiceDxHzzSWVzw/132");
                user.setGender(0);
                user.setUserPassword("12345678");
                user.setPhone("123");
                user.setEmail("123@qq.com");
                user.setTags("[]");
                user.setUserStatus(0);
                user.setUserRole(0);
                userList.add(user);
                if (j % batchSize == 0) {
                    break;
                }
            }
            // 异步执行
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                userService.saveBatch(userList, batchSize);
            },executorService);
            futureList.add(future);
        }
        //
        CompletableFuture.allOf(futureList.toArray(new CompletableFuture[]{})).join();
        //20秒 10万条
        stopWatch.stop();
        System.out.println(stopWatch.getTotalTimeMillis());
    }

    /**
     * 并发批量插入用户
     */
    @Test
    public void doConcurrencyInsertUsers1() {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        // 分十组
        int batchSize = 5000;
        int j = 0;
        List<CompletableFuture<Void>> futureList = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            List<User> userList = new ArrayList<>();
            while(true) {
                j++;
                User user = new User();
                user.setUsername("测试数据");
                user.setUserAccount("youyi" + j);
                user.setAvatarUrl("https://thirdwx.qlogo.cn/mmopen/vi_32/yYiceiaZZfa0xRHFE67FNHRFsFBWGicVUIuC8VMBACz7cC7vKqEq2ibLr5rzuMmPMoztwThpL0BOnNiceDxHzzSWVzw/132");
                user.setGender(0);
                user.setUserPassword("12345678" + j);
                user.setPhone("123");
                user.setEmail("123@qq.com");
                user.setTags("[]");
                user.setUserStatus(0);
                user.setUserRole(0);
                userList.add(user);
                if (j % batchSize == 0) {
                    break;
                }
            }
            // 异步执行
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                userService.saveBatch(userList, batchSize);
            }, executorService);
            futureList.add(future);
        }
        CompletableFuture.allOf(futureList.toArray(new CompletableFuture[]{})).join();
        // 20 秒 10 万条
        stopWatch.stop();
        System.out.println(stopWatch.getTotalTimeMillis());
    }
}
