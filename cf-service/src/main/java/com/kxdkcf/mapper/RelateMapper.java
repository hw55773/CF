package com.kxdkcf.mapper;

import com.kxdkcf.dto.RelateDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * BelongsProject:      demo-cf
 * BelongsPackage:      com.kxdkcf.mapper
 * Author:              wenhao
 * CreateTime:          2024-12-27  21:16
 * Description:         TODO
 * Version:             1.0
 */
@Mapper
public interface RelateMapper {
    /**
     * 查询评价了的用户列表
     *
     * @return 用户列表
     */
    @Select("select user_id from relate group by user_id")
    List<Long> getUsers();

    /**
     * 根据用户id查询相关关系数据
     *
     * @param id
     * @return
     */

    List<RelateDTO> getRelateList(Long id);

    void insert(Long userId, List<Long> topicIds);

    void insertByTopicId(Long topicId, List<Long> userIds);

    void deleteByTopicIds(List<Long> list);

    @Update("update relate set `index` =#{score} where topic_id=#{topicId} and user_id=#{userId}")
    void update(Long topicId, Long userId, double score);
}
