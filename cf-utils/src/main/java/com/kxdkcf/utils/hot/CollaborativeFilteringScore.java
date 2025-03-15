package com.kxdkcf.utils.hot;

public class CollaborativeFilteringScore {

    // 用户行为权重配置（需满足总和<=1）
    private static final double FAV_WEIGHT = 0.25;
    private static final double LIKE_WEIGHT = 0.25;
    private static final double REPLY_WEIGHT = 0.5;

    // 非线性转换参数
    private static final double REPLY_LOG_BASE = 2.0;
    private static final double MAX_REPLY_BOOST = 2.0;

    /**
     * 生成协同过滤专用评分（0-1区间）
     *
     * @param hasFav   是否收藏
     * @param hasLike  是否点赞
     * @param replyCnt 回复次数
     * @return 标准化偏好分数
     */
    public static double calculateCFScore(boolean hasFav,
                                          boolean hasLike,
                                          Long replyCnt) {
        // 基础行为得分
        double baseScore = (hasFav ? FAV_WEIGHT : 0)
                + (hasLike ? LIKE_WEIGHT : 0);

        // 回复次数非线性处理
        double replyBoost = Math.log(1 + replyCnt) / Math.log(REPLY_LOG_BASE);
        double replyScore = REPLY_WEIGHT * Math.min(replyBoost, MAX_REPLY_BOOST);

        // 综合评分
        double finalScore = baseScore + replyScore;
        return Math.min(Math.max(finalScore, 0.0), 1.0);
    }

}