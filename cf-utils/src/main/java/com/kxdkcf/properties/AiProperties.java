package com.kxdkcf.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * BelongsProject:      demo-cf
 * BelongsPackage:      com.kxdkcf.properties
 * Author:              wenhao
 * CreateTime:          2024-12-31  16:16
 * Description:         TODO
 * Version:             1.0
 */
@Data
@Component
@ConfigurationProperties(prefix = "kxdkcf.ai")
public class AiProperties {

    private String apiKey;
    private String url;

}
