package com.kxdkcf.utils.hot;

import java.util.HashMap;
import java.util.Map;

public class HotPostEvaluator {
    // 权重配置（点赞、收藏、评论）
    private final double likeWeight;
    private final double collectWeight;
    private final double commentWeight;

    // 阈值配置
    private final double hotThreshold;

    public HotPostEvaluator(double likeWeight,
                            double collectWeight,
                            double commentWeight,
                            double hotThreshold) {
        this.likeWeight = likeWeight;
        this.collectWeight = collectWeight;
        this.commentWeight = commentWeight;
        this.hotThreshold = hotThreshold;
    }

    /**
     * 计算Z-Score标准化值
     *
     * @param value  原始值
     * @param mean   平均值
     * @param stdDev 标准差
     * @return 标准化后的值
     */
    private double calculateZScore(double value, double mean, double stdDev) {
        if (stdDev == 0) return 0; // 避免除以零
        return (value - mean) / stdDev;
    }

    /**
     * 计算帖子热度
     *
     * @param likes    点赞数
     * @param collects 收藏数
     * @param comments 评论数
     * @param stats    统计数据（需包含各指标的均值和标准差）
     * @return 包含标准化值和总分的Map
     */
    public Map<String, Double> calculateHotness(
            Long likes,
            Long collects,
            Long comments,
            Map<String, Double[]> stats) {

        // 计算各维度Z-Score
        double likeScore = calculateZScore(likes,
                stats.get("likes")[0], stats.get("likes")[1]);

        double collectScore = calculateZScore(collects,
                stats.get("collects")[0], stats.get("collects")[1]);

        double commentScore = calculateZScore(comments,
                stats.get("comments")[0], stats.get("comments")[1]);

        // 计算加权总分
        double totalScore = likeWeight * likeScore
                + collectWeight * collectScore
                + commentWeight * commentScore;

        // 返回详细结果
        Map<String, Double> result = new HashMap<>();
        result.put("likeScore", likeScore);
        result.put("collectScore", collectScore);
        result.put("commentScore", commentScore);
        result.put("totalScore", totalScore);
        return result;
    }

    /**
     * 判断是否为热门帖子
     */
    public boolean isHotPost(double totalScore) {
        return totalScore >= hotThreshold;
    }

}