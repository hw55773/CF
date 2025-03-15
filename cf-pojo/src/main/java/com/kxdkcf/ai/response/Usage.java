package com.kxdkcf.ai.response;

import lombok.Data;

@Data
public class Usage {
    private int promptTokens;
    private int completionTokens;
    private int totalTokens;

}
