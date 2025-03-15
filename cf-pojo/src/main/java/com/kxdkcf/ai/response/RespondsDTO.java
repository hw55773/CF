package com.kxdkcf.ai.response;

import lombok.Data;

import java.util.List;

/**
 * BelongsProject:      demo-cf
 * BelongsPackage:      com.kxdkcf.ai
 * Author:              wenhao
 * CreateTime:          2024-12-31  15:50
 * Description:         接受ai对象
 * Version:             1.0
 */
@Data
public class RespondsDTO {
    private int code;
    private String message;
    private String sid;
    private List<Choice> choices;
    private Usage usage;
    private Error error;
}
