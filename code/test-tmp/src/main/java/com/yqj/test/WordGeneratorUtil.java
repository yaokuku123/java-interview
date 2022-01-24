package com.yqj.test;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

public class WordGeneratorUtil {

    /**
     * 生成指定大小的单词文件
     *
     * @param fileSizeLimit 文件大小限制
     * @param words         单词集合
     * @param path          文件路径
     * @throws IOException
     */
    public static void generateWord(long fileSizeLimit, String[] words,String path) throws IOException {
        // 规定单词文件的大小限制
        long count = 0;
        Random random = new Random();
        BufferedWriter writer = new BufferedWriter(new FileWriter(path));
        // 随机的向文件中写入指定大小的单词
        while (count < fileSizeLimit) {
            int index = random.nextInt(words.length);
            writer.write(words[index]);
            writer.newLine();
            count += words[index].length();
        }
        writer.flush();
        writer.close();
    }
}
