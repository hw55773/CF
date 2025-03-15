package com.kxdkcf.utils.hot;

import java.util.Random;

/**
 * BelongsProject:      demo-cf
 * BelongsPackage:      com.kxdkcf.utils.hot
 * Author:              wenhao
 * CreateTime:          2025-04-24  20:50
 */
public class RandomUtil {

    public static double createRandom() {
        // 创建随机数生成器（推荐用带种子的方式初始化）
        Random random = new Random(System.currentTimeMillis());

        // 步骤1：生成基础随机数（两种方法可选）
        double baseValue = Math.random();          // 方法1：使用Math.random()
        // double baseValue = random.nextDouble(); // 方法2：使用Random类

        // 步骤2：分区间生成原始值
        double originalValue;
        if (random.nextBoolean()) { // 50%概率选择负区间
            originalValue = random.nextDouble() - 1.5; // -1.5~-0.5
        } else {                    // 50%概率选择正区间
            originalValue = random.nextDouble() + 0.5; // 0.5~1.5
        }

        return originalValue;
    }
}
