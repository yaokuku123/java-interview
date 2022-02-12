package com.yqj.map;

import java.util.HashMap;
import java.util.Map;

public class TestMap {
    public static void main(String[] args) {
        Map<String,String> map = new HashMap<>();
        map.put("yorick","jun");
        System.out.println(map.get("yorick"));
    }
}
