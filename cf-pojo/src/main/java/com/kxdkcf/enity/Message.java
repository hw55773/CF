package com.kxdkcf.enity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * BelongsProject:      demo-cf
 * BelongsPackage:      com.kxdkcf.enity
 * Author:              wenhao
 * CreateTime:          2025-03-01  15:11
 * Description:         TODO
 * Version:             1.0
 */
@Data
public class Message {

    /**
     * 消息唯一id
     */
    private Long id;
    /**
     * 会话id
     */
    private Long dialogId;

    /**
     * 消息所属类型（0：ai,1:user）
     */
    private byte type;
    /**
     * 消息文本
     */
    private String content;
    /**
     * 消息创建时间
     */
    private LocalDateTime createTime;

}
