package com.yqj.jvm;

public class TestDynamicLoad {
    static {
        System.out.println("load TestDynamicLoad");
    }

    public static void main(String[] args) {
        new A();
        System.out.println("load test");
        B b = null;
    }
}

class A {
    static {
        System.out.println("load A");
    }

    public A() {
        System.out.println("init A");
    }
}

class B {
    static {
        System.out.println("load B");
    }

    public B() {
        System.out.println("init B");
    }
}
