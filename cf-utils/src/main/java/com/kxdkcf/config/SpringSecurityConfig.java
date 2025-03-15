package com.kxdkcf.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * BelongsProject:      demo-cf
 * BelongsPackage:      com.kxdkcf.config
 * Author:              wenhao
 * CreateTime:          2025-01-07  16:21
 * Description:         TODO
 * Version:             1.0
 */
@Configuration
public class SpringSecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {

        return new BCryptPasswordEncoder();
    }
}
