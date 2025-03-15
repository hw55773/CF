package com.kxdkcf.utils.hot;

public class ThemeRatingSystem {
    // 行为权重配置（需满足总和<=5）
    private static final double FAV_SCORE = 1.5;
    private static final double LIKE_SCORE = 1.0;
    private static final double MAX_REPLY_SCORE = 2.5;

    // 回复增益参数
    private static final double REPLY_LOG_BASE = 3.0;
    private static final double REPLY_SATURATION = 15; // 回复数饱和阈值

    public static double calculateRating(boolean hasFav,
                                         boolean hasLike,
                                         long replyCnt) {
        // 显式行为得分
        double explicitScore = (hasFav ? FAV_SCORE : 0)
                + (hasLike ? LIKE_SCORE : 0);

        // 回复数非线性处理
        double replyBoost = Math.log1p(replyCnt) / Math.log(REPLY_LOG_BASE);
        double normalizedReply = MAX_REPLY_SCORE *
                Math.min(replyBoost / REPLY_SATURATION, 1.0);

        return Math.min(explicitScore + normalizedReply, 5.0);
    }
}