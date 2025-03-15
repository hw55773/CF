package com.kxdkcf.mapper;

import com.kxdkcf.vo.TopicVO;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

/**
 * BelongsProject:      demo-cf
 * BelongsPackage:      com.kxdkcf.mapper
 * Author:              wenhao
 * CreateTime:          2025-03-12  16:18
 * Description:         TODO
 * Version:             1.0
 */
@Mapper
public interface LikeMapper {
    @Select("select count(*) from `like` where user_id=#{userId} and topic_id=#{topicId}")
    Integer selectByUserIdAndTopicId(Long userId, Long topicId);

    @Insert("insert into `like`(user_id, topic_id, is_liked, is_collected) VALUE " +
            "(#{userId},#{topicId},#{isLiked},#{isCollected})")
    void insert(Long userId, Long topicId, Boolean isLiked, Boolean isCollected);

    @Update("update `like` " +
            "set " +
            "is_liked=#{isLiked}," +
            "is_collected=#{isCollected} " +
            "where user_id=#{userId} " +
            "and topic_id=#{topicId}")
    void update(Long userId, Long topicId, Boolean isLiked, Boolean isCollected);

    List<TopicVO> selectCollectedByUserId(Long userId);

    @Delete("delete from `like` where user_id= #{userId}")
    void deleteByUserId(Long userId);

    void deleteByTopicIds(List<Long> topicIds);

    @Select("select is_liked,is_collected from `like` where topic_id =#{topicId} and user_id=#{userId}")
    Map<String, Object> selectLikeAndFav(Long topicId, Long userId);
}
