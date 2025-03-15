package com.kxdkcf.properties;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * BelongsProject:      demo-cf
 * BelongsPackage:      com.kxdkcf.properties
 * Author:              wenhao
 * CreateTime:          2024-12-29  14:58
 * Description:         TODO
 * Version:             1.0
 */
@Data
@Component
@Slf4j
@ConfigurationProperties(prefix = "kxdkcf.jwt")
public class JwtProperties {

    private String userTokenKey;
    private long ttl;
    private String tokenName;

}
