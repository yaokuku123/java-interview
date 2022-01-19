package com.yqj.jvm;

import java.util.Collections;

public class AllocOnStack {
    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        for (int i = 0; i < 100000000 ; i++) {
            alloc();
        }
        long end = System.currentTimeMillis();
        System.out.println(end - start);
    }

    private static void alloc() {
        User user = new User();
        user.setUsername("yorick");
        user.setPassword("123");
    }
}
