package com.yqj.service;

import com.spring.*;

@Component("userService")
public class UserService implements BeanNameAware, InitializingBean {

    @Autowired
    private OrderService orderService;
    private String beanName;

    public void test() {
        System.out.println(beanName + "   ,    " + orderService);
    }

    @Override
    public void setBeanName(String name) {
        beanName = name;
    }

    @Override
    public void afterPropertiesSet() {
        System.out.println("init...");
    }
}
