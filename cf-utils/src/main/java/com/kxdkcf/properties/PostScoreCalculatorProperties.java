package com.kxdkcf.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * BelongsProject:      demo-cf
 * BelongsPackage:      com.kxdkcf.properties
 * Author:              wenhao
 * CreateTime:          2025-04-24  15:06
 */
@Data
@Component
@ConfigurationProperties(prefix = "kxdkcf.calculator")
public class PostScoreCalculatorProperties {
    private double favWeight;
    private double likeWeight;
    private double replyWeight;
    private double hotWeight;
}
