package com.kxdkcf.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * BelongsProject:      demo-cf
 * BelongsPackage:      com.kxdkcf.properties
 * Author:              wenhao
 * CreateTime:          2024-12-28  22:00
 * Description:         拦截路径属性
 * Version:             1.0
 */
@Component
@Data
@ConfigurationProperties(prefix = "kxdkcf.path")
public class PathHandler {

    private List<String> excludePathList;

    private List<String> addPathList;

}
