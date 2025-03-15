package com.kxdkcf.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

@Configuration
public class AsyncConfig {

    @Bean("taskExecutor")
    public TaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(15);      // 核心线程数
        executor.setMaxPoolSize(30);      // 最大线程数
        executor.setQueueCapacity(100);   // 队列容量
        executor.setThreadNamePrefix("Async-"); // 线程名前缀
        executor.setKeepAliveSeconds(300);
        executor.setPrestartAllCoreThreads(true);
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();
        return executor;
    }
}