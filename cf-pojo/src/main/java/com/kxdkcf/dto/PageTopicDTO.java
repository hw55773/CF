package com.kxdkcf.dto;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

/**
 * BelongsProject:      demo-cf
 * BelongsPackage:      com.kxdkcf.dto
 * Author:              wenhao
 * CreateTime:          2024-12-27  22:05
 * Description:         TODO
 * Version:             1.0
 */
@Data
public class PageTopicDTO {

    private Long userId;
    private String name;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime begin;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime end;
    private Integer page;
    private Integer pageSize;

}
