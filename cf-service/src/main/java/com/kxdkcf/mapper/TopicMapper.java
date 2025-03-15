package com.kxdkcf.mapper;

import com.kxdkcf.enity.Topic;
import com.kxdkcf.vo.TopicDetailVO;
import com.kxdkcf.vo.TopicVO;
import org.apache.ibatis.annotations.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * BelongsProject:      demo-cf
 * BelongsPackage:      com.kxdkcf.mapper
 * Author:              wenhao
 * CreateTime:          2024-12-27  22:42
 * Description:         TODO
 * Version:             1.0
 */
@Mapper
public interface TopicMapper {


    List<TopicVO> getTopicList(List<Long> recommend, String name, LocalDateTime begin, LocalDateTime end);


    void insert(Topic topic);

    @Select("select is_liked,is_collected from `like` where user_id=#{userId} and topic_id=#{topicId}")
    Map<String, Boolean> selectIsLike(Long topicId, Long userId);


    void update(List<Topic> list);

    @Select("select id from topics")
    List<Long> getAllTopicId();

    TopicDetailVO selectTopicDetails(Long topicId);

    @Select("select id, reply_count from topics where id=#{topicId}")
    Topic selectTopicById(Long topicId);

    List<TopicVO> selectCollectedByUserId(Long userId);

    @Delete("delete from topics where user_id=#{userId}")
    void deleteByUserId(Long userId);

    @Select("select id, title, content, favorites_number, create_time, update_time, user_id, like_count, reply_count, is_hot from topics where user_id=#{userId}")
    List<Topic> selectTopicByUserId(Long userId);

    @Select("select id, favorites_number,like_count,reply_count from topics")
    @Results({
            @Result(id = true, column = "id", property = "id"),
            @Result(column = "favorites_number", property = "favoritesNumber"),
            @Result(column = "like_count", property = "likeCount"),
            @Result(column = "reply_count", property = "replyCount"),
    })
    List<Topic> selectCount();

    @Select("select max(favorites_number) as fav_max from topics")
    Map<String, Object> selectMaxFav();

    @Select("select max(like_count) as like_max from topics")
    Map<String, Object> selectMaxLike();

    @Select("select max(reply_count) as reply_max from topics")
    Map<String, Object> selectMaxReply();

    @Select("select id, title, content, favorites_number, create_time, update_time, user_id, like_count, reply_count, is_hot from topics")
    List<Topic> selectAllTopic();
}
