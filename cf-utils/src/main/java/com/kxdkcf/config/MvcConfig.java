package com.kxdkcf.config;

import com.kxdkcf.interceptor.UserTokenInterceptor;
import com.kxdkcf.properties.PathHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * BelongsProject:      demo-cf
 * BelongsPackage:      com.kxdkcf.config
 * Author:              wenhao
 * CreateTime:          2025-01-01  20:12
 * Description:         Wvc配置
 * Version:             1.0
 */
@RequiredArgsConstructor
@Configuration
public class MvcConfig implements WebMvcConfigurer {

    private final UserTokenInterceptor userTokenInterceptor;
    private final PathHandler pathHandler;
    @Value("${kxdkcf.file.upload-dir}")
    private String uploadDir;

    //添加拦截器
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(userTokenInterceptor)
                .addPathPatterns(pathHandler.getAddPathList())
                .excludePathPatterns(pathHandler.getExcludePathList())
                .order(0);
    }


    //添加静态资源拦截
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/images/**")
                .addResourceLocations("file:" + uploadDir + "/");
    }


    @Override
    public void addCorsMappings(CorsRegistry registry) {
        WebMvcConfigurer.super.addCorsMappings(registry);
    }
}
