package com.yqj.jvm;

import org.openjdk.jol.info.ClassLayout;

public class JolSample {
    public static void main(String[] args) {
        ClassLayout layout1 = ClassLayout.parseInstance(new Object());
        System.out.println(layout1.toPrintable());

        System.out.println();
        ClassLayout layout2 = ClassLayout.parseInstance(new int[]{});
        System.out.println(layout2.toPrintable());

        System.out.println();
        ClassLayout layout3 = ClassLayout.parseInstance(new SampleA());
        System.out.println(layout3.toPrintable());
    }
}

class SampleA {
    int id;
    String name;
    byte b;
    Object o;
}
