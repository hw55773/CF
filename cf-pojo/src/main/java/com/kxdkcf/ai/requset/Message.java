package com.kxdkcf.ai.requset;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// 消息类
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Message {
    /**
     * 角色(必要)
     */
    private String role;
    /**
     * 内容(必要的)
     */
    private String content;
}