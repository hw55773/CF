package com.kxdkcf.config;

import com.kxdkcf.properties.HotPostEvaluatorProperties;
import com.kxdkcf.properties.PostScoreCalculatorProperties;
import com.kxdkcf.utils.hot.HotPostEvaluator;
import com.kxdkcf.utils.hot.PostScoreCalculator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * BelongsProject:      demo-cf
 * BelongsPackage:      com.kxdkcf.config
 * Author:              wenhao
 * CreateTime:          2025-04-12  14:58
 */
@Configuration
public class CommonConfig {

    @Bean
    public HotPostEvaluator hotPostEvaluator(HotPostEvaluatorProperties hotPostEvaluatorProperties) {

        return new HotPostEvaluator(hotPostEvaluatorProperties.getLikeWeight(),
                hotPostEvaluatorProperties.getCollectWeight(),
                hotPostEvaluatorProperties.getCommentWeight(),
                hotPostEvaluatorProperties.getHotThreshold());
    }

    @Bean
    public PostScoreCalculator postScoreCalculator(PostScoreCalculatorProperties properties) {

        return new PostScoreCalculator(properties.getFavWeight(),
                properties.getLikeWeight(),
                properties.getReplyWeight(),
                properties.getHotWeight()
        );
    }

}
