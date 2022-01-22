package com.yqj;

import com.spring.YqjApplicationContext;
import com.yqj.config.AppConfig;
import com.yqj.service.UserService;

public class TestDemo {
    public static void main(String[] args) {
        YqjApplicationContext applicationContext = new YqjApplicationContext(AppConfig.class);
        UserService userService = (UserService) applicationContext.getBean("userService");
        userService.test();
    }
}
