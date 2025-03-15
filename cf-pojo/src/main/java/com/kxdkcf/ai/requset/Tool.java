package com.kxdkcf.ai.requset;

import lombok.Data;

// 工具类
@Data
public class Tool {
    /**
     * web_search或者function
     */
    private String type;
    /**
     * Function 类包含 name、description 和 parameters 字段，其中 parameters 使用 Object 类型，因为它可能会是不同格式的 JSON 对象，具体根据 json schema 来解析。
     */
    private Function function;
    /**
     * WebSearch 类包含 enable 字段，表示是否启用 Web 搜索。
     */
    private WebSearch web_search;
}