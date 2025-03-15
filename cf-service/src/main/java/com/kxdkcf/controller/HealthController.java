package com.kxdkcf.controller;

import com.kxdkcf.context.ThreadLocalValueUser;
import com.kxdkcf.service.IHealthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * BelongsProject:      demo-cf
 * BelongsPackage:      com.kxdkcf.controller
 * Author:              wenhao
 * CreateTime:          2025-03-10  13:32
 * Description:         TODO
 * Version:             1.0
 */
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/health")
@Tag(name = "健康相关接口")
public class HealthController {

    private final IHealthService healthService;

    public ResponseEntity<?> healthKnowledge() {

        return healthService.getHealthKnowledge();
    }

    /**
     * 获取健康分析
     *
     * @return 健康分析
     */
    @Operation(summary = "获取健康分析")
    @GetMapping("/analyse")
    public ResponseEntity<?> healthAnalyse() {

        Long userId = ThreadLocalValueUser.getThreadLocalValue(Long.class);
        return healthService.getHealthAnalyse(userId);
    }

    /**
     * 获取健康推荐信息
     *
     * @return 健康推荐信息
     */
    @Operation(summary = "获取健康推荐信息")
    @GetMapping("/recommend")
    public ResponseEntity<?> healthRecommendations() {

        Long userId = ThreadLocalValueUser.getThreadLocalValue(Long.class);

        return healthService.getHealthRecommendations(userId);

    }
}
