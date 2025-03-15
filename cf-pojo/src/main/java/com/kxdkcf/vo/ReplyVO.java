package com.kxdkcf.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.kxdkcf.enity.User;
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
public class ReplyVO {
    private Long id;
    private String content;
    private User user;
    private Long topicId;
    private Boolean isLiked;
    private Long likeCount;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}
