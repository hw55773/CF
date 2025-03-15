package com.kxdkcf.mapper;

import com.kxdkcf.enity.Reply;
import com.kxdkcf.vo.ReplyVO;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * BelongsProject:      demo-cf
 * BelongsPackage:      com.kxdkcf.mapper
 * Author:              wenhao
 * CreateTime:          2025-03-14  16:44
 * Description:         TODO
 * Version:             1.0
 */
@Mapper
public interface ReplyMapper {

    List<ReplyVO> getReplyByTopicId(Long topicId);


    void insert(Reply reply);

    @Select("select  * from reply where id=#{replyId}")
    Reply selectById(Long replyId);

    @Select("select is_liked from reply_liked where user_id=#{userId} and reply_id=#{replyId}")
    Boolean isLiked(Long userId, Long replyId);

    void update(Reply reply);

    @Update("update reply_liked set is_liked=#{aFalse} where user_id=#{userId} and reply_id=#{replyId}")
    void updateLiked(Long userId, Long replyId, boolean aFalse);

    @Insert("insert into reply_liked ( user_id, reply_id, is_liked)" +
            "value (#{userId},#{replyId},#{isLiked})")
    void insertReplyLiked(Long userId, Long replyId, boolean isLiked);

    @Delete("delete from reply where id=#{replyId}")
    void deleteByReplyId(Long replyId);

    @Delete("delete from reply_liked where reply_id=#{replyId}")
    void deleteReplyLikedByReplyId(Long replyId);

    @Delete("delete from relate where user_id=#{userId}")
    void deleteByUserId(Long userId);

    @Delete("delete from reply_liked where user_id=#{userId}")
    void deleteReplyLikedByUserId(Long userId);

    @Select("select count(*) from reply where topic_id=#{topicId} and user_id=#{userId}")
    Long getCount(Long topicId, Long userId);
}
