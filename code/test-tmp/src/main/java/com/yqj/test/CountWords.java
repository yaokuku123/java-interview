package com.yqj.test;

import java.io.*;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantLock;

public class CountWords {

    private ReentrantLock lock = new ReentrantLock();
    private ConcurrentHashMap<String, Integer> map = new ConcurrentHashMap<>();
    private PriorityQueue<Map.Entry<String, Integer>> queue = new PriorityQueue<>(Comparator.comparingInt(Map.Entry::getValue));
    private CountDownLatch countDownLatch;

    /**
     * 统计k个高频单词
     *
     * @param k k个高频单词
     * @param path 文件路径
     * @return 频率前k高的单词集合
     */
    public Map<String,Integer> countKHighWords(int k,String path) throws IOException, InterruptedException {
        Map<String,Integer> res = new HashMap<>();
        // 统计文件单词行数
        String command = "wc -l " + path;
        long lineNum = Long.parseLong(executeLinuxCmd(command).split(" ")[1]);
        System.out.println("文件单词个数: " + lineNum);
        //获取CPU数量
        int nThreads = Runtime.getRuntime().availableProcessors();
        long workNum = lineNum / nThreads;
        int coreNum = lineNum % nThreads == 0 ? nThreads : nThreads + 1;
        //创建线程池
        ThreadPoolExecutor threadPool = new ThreadPoolExecutor(coreNum, coreNum, 0L,
                TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
        countDownLatch = new CountDownLatch(coreNum);
        //分发任务
        for (int i = 0; i < coreNum; i++) {
            final int innerI = i;
            threadPool.execute(() -> {
                try {
                    //每个线程统计任务
                    BufferedReader reader = new BufferedReader(new FileReader(path));
                    reader.lines()
                            .skip(innerI * workNum)
                            .limit(workNum)
                            .forEach((str)->{
                                //虽然ConcurrentHashMap是线程安全的，但是put get两个方法合一起执行就不安全了，要加锁
                                lock.lock();
                                try {
                                    map.put(str, map.getOrDefault(str, 0) + 1);
                                } finally {
                                    lock.unlock();
                                }
                            });
                    reader.close();
                    countDownLatch.countDown();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
        // 等待任务完成
        countDownLatch.await();
        threadPool.shutdown();
        System.out.println("========全部单词频率===========");
        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            System.out.println(entry.getKey() + " 单词的出现频率: " + entry.getValue());
        }

        // 统计最高频率的k个单词
        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            if (queue.size() < k) {
                queue.offer(entry);
            } else if (queue.peek().getValue() < entry.getValue()) {
                queue.poll();
                queue.offer(entry);
            }
        }
        for (Map. Entry<String, Integer> entry : queue) {
            res.put(entry.getKey(),entry.getValue());
        }
        return res;
    }

    /**
     * 执行Linux命令
     * @param cmd 命令
     * @return 执行结果
     */
    private String executeLinuxCmd(String cmd) {
        System.out.println("执行命令[ " + cmd + "]");
        Runtime run = Runtime.getRuntime();
        try {
            Process process = run.exec(cmd);
            String line;
            BufferedReader stdoutReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuffer out = new StringBuffer();
            while ((line = stdoutReader.readLine()) != null ) {
                out.append(line);
            }
            try {
                process.waitFor();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            process.destroy();
            return out.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
