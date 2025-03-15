package com.kxdkcf.service;

import org.springframework.http.ResponseEntity;

/**
 * BelongsProject:      demo-cf
 * BelongsPackage:      com.kxdkcf.service
 * Author:              wenhao
 * CreateTime:          2025-03-10  13:35
 * Description:         TODO
 * Version:             1.0
 */
public interface IHealthService {
    ResponseEntity<?> getHealthKnowledge();

    ResponseEntity<?> getHealthAnalyse(Long userId);

    ResponseEntity<?> getHealthRecommendations(Long userId);
}
