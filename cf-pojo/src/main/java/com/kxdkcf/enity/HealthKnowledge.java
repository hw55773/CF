package com.kxdkcf.enity;

import lombok.Data;

/**
 * BelongsProject:      demo-cf
 * BelongsPackage:      com.kxdkcf.enity
 * Author:              wenhao
 * CreateTime:          2025-03-25  14:34
 * Description:         TODO
 * Version:             1.0
 */
@Data
public class HealthKnowledge {

    private Long id;
    /**
     * 标题
     */
    private String title;
    /**
     * 分类：0:饮食健康，1:运动健身，2:心理健康
     */
    private Integer category;
    /**
     * 内容
     */
    private String content;
    /**
     * 来源
     */
    private String source;
    /**
     * 图像url
     */
    private String image;
}
