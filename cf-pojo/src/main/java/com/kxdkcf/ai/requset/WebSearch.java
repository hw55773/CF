package com.kxdkcf.ai.requset;

import lombok.Data;

// WebSearch 类型工具
@Data
public class WebSearch {
    private Boolean enable;
    /**
     * deep或normal deep:深度搜索 / normal:标准搜索,不同的搜索策略，效果不同，并且token消耗也有差异
     */
    private String search_mode;
}