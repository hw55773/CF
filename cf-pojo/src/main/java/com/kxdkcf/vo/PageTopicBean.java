package com.kxdkcf.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * BelongsProject:      demo-cf
 * BelongsPackage:      com.kxdkcf.vo
 * Author:              wenhao
 * CreateTime:          2024-12-27  23:35
 * Description:         TODO
 * Version:             1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageTopicBean {
    private Long total;
    private List<TopicVO> topicList;
}
