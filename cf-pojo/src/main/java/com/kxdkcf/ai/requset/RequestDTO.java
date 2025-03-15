package com.kxdkcf.ai.requset;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * BelongsProject:      demo-cf
 * BelongsPackage:      com.kxdkcf.ai
 * Author:              wenhao
 * CreateTime:          2024-12-31  15:49
 * Description:         请求ai的对象
 * Version:             1.0
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RequestDTO {
    /**
     * 使用的模型（必要的）
     */
    private String model;
    private String user;
    /**
     * Message 类对应 messages 数组中的每个消息对象，包含 role 和 content 字段。（必要的）
     */
    private List<Message> messages;
    /**
     * 核采样阈值，用于决定结果随机性，取值越高随机性越强，即相同的问题得到的不同答案的可能性越高。
     * 取值范围 (0，1]，默认为0.5
     */
    private Double temperature;
    /**
     * 平衡生成文本的质量和多样性，较小的 k 值会减少随机性，使得输出更加稳定；
     * 而较大的 k 值会增加随机性，产生更多新颖的输出。
     * 取值范围[1, 6]，默认为4
     */
    private Integer top_k;
    private Boolean stream;
    private Integer max_tokens;
    private Integer presence_penalty;
    private Integer frequency_penalty;
    /**
     * Tool 类是一个包含工具信息的对象，里面有 function 和 web_search 字段，分别是 Function 类型和 WebSearch 类型的对象。
     */
    private List<Tool> tools;
    /**
     * ResponseFormat 类用于处理 response_format 字段。
     */
    private ResponseFormat response_format;
    /**
     * suppress_plugin 是一个字符串数组，表示需要被抑制的插件列表。
     */
    private List<String> suppress_plugin;
}










