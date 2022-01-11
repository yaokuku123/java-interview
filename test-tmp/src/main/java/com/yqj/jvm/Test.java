package com.yqj.jvm;

import static java.lang.Math.max;

public class Test {
    public static void main(String[] args) {
        int[] coins = {1, 5};
        int amount = 6;
        int[] dp = new int[amount + 1];
        dp[0] = 1;
        for (int i = 0; i < coins.length; i++) { // 遍历物品
            for (int j = coins[i]; j <= amount; j++) { // 遍历背包容量
                dp[j] += dp[j - coins[i]];
            }
        }
//        for (int j = 0; j <= amount; j++) { // 遍历背包容量
//            for (int i = 0; i < coins.length; i++) { // 遍历物品
//                if (j - coins[i] >= 0) dp[j] += dp[j - coins[i]];
//            }
//        }
        System.out.println(dp[amount]);
        
    }
}
