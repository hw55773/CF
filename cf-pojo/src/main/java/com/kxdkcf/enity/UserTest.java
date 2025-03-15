package com.kxdkcf.enity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * BelongsProject:      demo-cf
 * BelongsPackage:      com.kxdkcf.enity
 * Author:              wenhao
 * CreateTime:          2024-12-30  22:21
 * Description:         TODO
 * Version:             1.0
 */
@Data
public class UserTest {

    private String name;
    private Long age;
    private Topic topic;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime time;

}
