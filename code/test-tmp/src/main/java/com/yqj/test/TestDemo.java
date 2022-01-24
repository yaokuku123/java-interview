package com.yqj.test;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public class TestDemo {

    private static final long SIZE_MB = 1024 * 1024;

    private static final String PATH = System.getProperty("user.dir") + File.separator + "data.txt";

    private static String[] words = {"java", "c++", "python", "golang", "ruby",
            "solidity", "hello", "world", "system", "network", "struct"};


    public static void main(String[] args) throws IOException, InterruptedException {
        //生成指定大小的单词 50MB
        WordGeneratorUtil.generateWord(50 * SIZE_MB, words,PATH);
        //指定查找前k个最大频率的字母
        int k = 2;
        Map<String, Integer> res = new CountWords().countKHighWords(k, PATH);
        System.out.println("========前"+ k +"高的单词频率===========");
        res.forEach((key,value)->{
            System.out.println(key + " 单词的出现频率: " + value);
        });
    }
}
