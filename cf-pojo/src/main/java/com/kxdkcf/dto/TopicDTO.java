package com.kxdkcf.dto;

import lombok.Data;

/**
 * BelongsProject:      demo-cf
 * BelongsPackage:      com.kxdkcf.dto
 * Author:              wenhao
 * CreateTime:          2024-12-27  13:15
 * Description:         主题贴传输对象
 * Version:             1.0
 */
@Data
public class TopicDTO {

    /**
     * 主题id
     */
    private Long id;
    /**
     * 标题内容
     */
    private String title;
    /**
     * 主题内容
     */
    private String content;
    /**
     * 话题被收藏的数量。
     */
    private Long favoritesNumber;
    /**
     * 点赞数
     */
    private Long likeCount;
    /**
     * 回复数
     */
    private Long replyCount;
    /**
     * 是否热门，0：不热门，1：热门
     */
    private Boolean isHot;

    /**
     * 是否点赞
     */
    private Boolean isLiked;
    /**
     * 是否收藏
     */
    private Boolean isCollected;


}
