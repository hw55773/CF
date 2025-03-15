package com.kxdkcf.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * BelongsProject:      demo-cf
 * BelongsPackage:      com.kxdkcf.dto
 * Author:              wenhao
 * CreateTime:          2024-12-27  15:35
 * Description:         TODO
 * Version:             1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RelateDTO {
    private Long userId;
    private Long topicId;
    private Double index;
}