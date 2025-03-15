package com.kxdkcf.service;

import com.kxdkcf.Result.Result;
import com.kxdkcf.dto.PageTopicDTO;

/**
 * BelongsProject:      demo-cf
 * BelongsPackage:      com.kxdkcf.service
 * Author:              wenhao
 * CreateTime:          2024-12-27  19:30
 * Description:         TODO
 * Version:             1.0
 */
public interface IRecommendService {
    default Result queryTopic(PageTopicDTO pageTopicDTO) {
        return null;
    }

    default Result quryByTopicId(Long topicId) {
        return null;
    }
}
