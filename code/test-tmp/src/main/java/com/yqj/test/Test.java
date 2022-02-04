package com.yqj.test;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

public class Test {
    public static void main(String[] args) {
        int[][] nums = new int[][]{{1, 2},{3,4}};
        Arrays.sort(nums,(num1,num2) -> {
            if (num1[0] == num2[0]) {
                return num1[1] - num2[1];
            } else {
                return num2[0] - num1[0];
            }
        });
        Arrays.stream(nums).forEach((o)-> System.out.print(o + " "));
    }
}
