package com.kxdkcf.ai.response;

import lombok.Data;

@Data
public class Error {
    private String message;
    private String type;
    private String param;
    private Integer code;
}
