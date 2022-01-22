package com.yqj.jvm;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class OOMTest {

    public static void main(String[] args) {
        List<Object> list = new ArrayList<>();
        int i = 0, j = 0;
        while (true) {
            list.add(new User(String.valueOf(i++), UUID.randomUUID().toString()));
        }
    }
}
