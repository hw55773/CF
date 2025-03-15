package com.kxdkcf.enity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * BelongsProject:      demo-cf
 * BelongsPackage:      com.kxdkcf.enity
 * Author:              wenhao
 * CreateTime:          2024-12-27  13:16
 * Description:         主题帖实体对象
 * Version:             1.0
 */
@Data
public class Topic {

    /**
     * 主题的唯一标识符。
     */
    private Long id;

    /**
     * 话题的标题。
     */
    private String title;

    /**
     * 话题的详细内容。
     */
    private String content;

    /**
     * 话题被收藏的数量。
     */
    private Long favoritesNumber;

    /**
     * 帖子所属用户
     */
    private Long userId;
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
     * 主题创建时间。
     * 记录话题被创建的时间点。
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    /**
     * 主题信息最后更新时间。
     * 记录话题信息最后一次被更新的时间点。
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}
