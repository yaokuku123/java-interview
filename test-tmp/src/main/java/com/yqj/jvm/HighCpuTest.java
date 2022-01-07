package com.yqj.jvm;

public class HighCpuTest {
    public int compute() { //一个方法对应一块栈帧内存区域
        int a = 1;
        int b = 2;
        int c = (a + b) * 10;
        return c;
    }

    public static void main(String[] args) {
        HighCpuTest highCPU = new HighCpuTest();
        while (true) {
            highCPU.compute();
        }
    }
}
