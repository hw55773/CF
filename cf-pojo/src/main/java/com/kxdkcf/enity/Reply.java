package com.kxdkcf.enity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * BelongsProject:      demo-cf
 * BelongsPackage:      com.kxdkcf.enity
 * Author:              wenhao
 * CreateTime:          2025-03-13  12:43
 * Description:         TODO
 * Version:             1.0
 */
@Data
public class Reply {
    private Long id;
    private String content;
    private Long userId;
    private Long topicId;
    private Boolean isLiked;
    private Long likeCount;
    private LocalDateTime createTime;
}
