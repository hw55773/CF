package com.kxdkcf.ai.requset;

import lombok.Data;

// Function 类型工具
@Data
public class Function {
    private String name;
    private String description;
    private Object parameters; // 用 Object 因为它会遵循 JSON schema 格式，可以灵活处理
}