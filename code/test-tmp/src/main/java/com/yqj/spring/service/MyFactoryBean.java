package com.yqj.spring.service;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.stereotype.Component;

@Component
public class MyFactoryBean implements FactoryBean {
    @Override
    public Object getObject() throws Exception {
        return new OrderService();
    }

    @Override
    public Class<?> getObjectType()  {
        return OrderService.class;
    }
}
