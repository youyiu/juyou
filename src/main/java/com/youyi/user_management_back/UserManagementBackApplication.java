package com.youyi.user_management_back;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@MapperScan("com.youyi.user_management_back.mapper")
public class UserManagementBackApplication {
    public static void main(String[] args) {
        SpringApplication.run(UserManagementBackApplication.class, args);
    }

}
