package com.kxdkcf.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

/**
 * BelongsProject:      demo-cf
 * BelongsPackage:      com.kxdkcf.config
 * Author:              wenhao
 * CreateTime:          2024-12-30  17:54
 * Description:         TODO
 * Version:             1.0
 */
@Configuration
public class WebSocketConfig {

    @Bean
    public ServerEndpointExporter serverEndpointExporter() {

        return new ServerEndpointExporter();
    }
}
