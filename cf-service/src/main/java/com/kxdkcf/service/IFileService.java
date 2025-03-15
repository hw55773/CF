package com.kxdkcf.service;

import com.kxdkcf.dto.HealthDataDTO;

/**
 * BelongsProject:      demo-cf
 * BelongsPackage:      com.kxdkcf.service
 * Author:              wenhao
 * CreateTime:          2025-03-16  16:51
 * Description:         TODO
 * Version:             1.0
 */
public interface IFileService {
    void processingOfHealthData(HealthDataDTO healthDataDTO, Long userId);
}
