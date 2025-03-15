package com.kxdkcf.enity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * BelongsProject:      demo-cf
 * BelongsPackage:      com.kxdkcf.enity
 * Author:              wenhao
 * CreateTime:          2025-04-03  14:15
 */
@Data
public class HealthRecommendation {
    private Long id;
    private Long userId;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private String content;
}
