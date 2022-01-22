package com.yqj.jvm;

import lombok.Data;

public class MathTest {
    public static final int initData = 666;
    public static User user = new User();


    public int compute() { //一个方法对应一块栈帧内存区域
        int a = 1;
        int b = 2;
        int c = (a + b) * 10;
        return c;
    }

    public static void main(String[] args) {
        MathTest mathTest = new MathTest();
        while (true) {
            mathTest.compute();
        }
    }
}

@Data
class User {
    private String username;
    private String password;

    public User() {
    }

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }
}
