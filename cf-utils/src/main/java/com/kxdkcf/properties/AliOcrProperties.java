package com.kxdkcf.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * BelongsProject:      demo-cf
 * BelongsPackage:      com.kxdkcf.properties
 * Author:              wenhao
 * CreateTime:          2025-03-19  19:02
 * Description:         TODO
 * Version:             1.0
 */
@Data
@Component
@ConfigurationProperties(prefix = "kxdkcf.ali-ocr")
public class AliOcrProperties {
    private String id;
    private String key;
}
