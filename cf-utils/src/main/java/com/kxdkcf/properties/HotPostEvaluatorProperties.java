package com.kxdkcf.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * BelongsProject:      demo-cf
 * BelongsPackage:      com.kxdkcf.properties
 * Author:              wenhao
 * CreateTime:          2025-04-12  15:03
 */
@Data
@Component
@ConfigurationProperties(prefix = "kxdkcf.evaluator")
public class HotPostEvaluatorProperties {
    private double likeWeight;
    private double collectWeight;
    private double commentWeight;
    private double hotThreshold;
}
