package com.kxdkcf.utils.hot;

import lombok.Data;

@Data
public class PostScoreCalculator {

    // 各指标权重
    private final double FAV_WEIGHT;//0.3
    private final double LIKE_WEIGHT;//0.3
    private final double REPLY_WEIGHT;//0.2
    private final double HOT_WEIGHT;//0.2
    // 各指标的最大值
    private long MAX_FAVORITES;
    private long MAX_LIKES;
    private long MAX_REPLIES;

    // 归一化方法（线性处理）
    private static double normalize(int value, long max) {
        if (max <= 0) return 0.0;
        return Math.min((double) value / max, 1.0);
    }

    /**
     * 计算主题帖评分（0-5分）
     *
     * @param favorites 收藏数
     * @param likes     点赞数
     * @param replies   回复数
     * @param isHot     是否热门主题
     * @return 标准化后的评分（0.0~5.0）
     */
    public double calculateScore(int favorites, int likes, int replies, boolean isHot) {
        // 归一化处理各指标到[0,1]区间
        double favNorm = normalize(favorites, MAX_FAVORITES);
        double likeNorm = normalize(likes, MAX_LIKES);
        double replyNorm = normalize(replies, MAX_REPLIES);

        // 计算加权得分
        double score = (FAV_WEIGHT * favNorm)
                + (LIKE_WEIGHT * likeNorm)
                + (REPLY_WEIGHT * replyNorm);

        // 热门主题附加权重
        if (isHot) {
            score += HOT_WEIGHT;
        }

        // 转换为0-5分制并确保边界
        double finalScore = score * 5;
        return Math.min(Math.max(finalScore, 0.0), 5.0);
    }

}