package com.youyi.user_management_back.job;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youyi.user_management_back.mapper.UserMapper;
import com.youyi.user_management_back.model.domain.User;
import com.youyi.user_management_back.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 缓存预热任务
 */
@Component
@Slf4j
public class PreCacheJob {

    @Resource
    private RedisTemplate<String,Object> redisTemplate;

    @Resource
    private UserService userService;

    @Resource
    private RedissonClient redissonClient;

    //重点用户
    private List<Long> keyUserList = Arrays.asList(1L,2L);

    //每天执行，预热推荐用户
    @Scheduled(cron = "0 59 23 * * ?")
    public void doCacheRecommendUser() {
        RLock lock = redissonClient.getLock("jiaoyou:precachejob:docache:lock");
        try {
            //只有一个线程
            if (lock.tryLock(0,30000, TimeUnit.MICROSECONDS)) {
                for (Long userId : keyUserList) {
                    String redisKey = String.format("youpao:user:recommend:%s", userId);
                    LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
                    Page<User> userPage = userService
                            .page(new Page<>(1,20),queryWrapper);
                    // 2.2 写入缓存
                    try {
                        redisTemplate.opsForValue().set(redisKey,userPage,10000, TimeUnit.MILLISECONDS);
                    } catch (Exception e) {
                        log.error("redis set key error",e);
                    }
                }
            }
        } catch (InterruptedException e) {
            log.error("tryLock error",e);
        }finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

}
