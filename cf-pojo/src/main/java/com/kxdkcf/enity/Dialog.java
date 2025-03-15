package com.kxdkcf.enity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * BelongsProject:      demo-cf
 * BelongsPackage:      com.kxdkcf.enity
 * Author:              wenhao
 * CreateTime:          2025-03-01  15:07
 * Description:         TODO
 * Version:             1.0
 */
@Data
public class Dialog {
    /**
     * 会话唯一id
     */
    private Long id;
    /**
     * 用户id
     */
    private Long userId;
    /**
     * 会话主题
     */
    private String title;
    /**
     * 会话创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
    /**
     * 会话更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

}
