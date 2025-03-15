package com.kxdkcf.enity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * BelongsProject:      demo-cf
 * BelongsPackage:      com.kxdkcf.enity
 * Author:              wenhao
 * CreateTime:          2025-03-20  14:57
 * Description:         TODO
 * Version:             1.0
 */
@Data
public class HealthData {
    /**
     * 唯一id
     */
    private Long id;
    /**
     * 用户id
     */
    private Long userId;
    /**
     * 健康状态内容
     */
    private String content;
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

}
