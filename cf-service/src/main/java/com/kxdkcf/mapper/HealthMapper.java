package com.kxdkcf.mapper;

import com.kxdkcf.annotation.AutoTime;
import com.kxdkcf.constant.OperationType;
import com.kxdkcf.enity.HealthData;
import com.kxdkcf.enity.HealthKnowledge;
import com.kxdkcf.enity.HealthRecommendation;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * BelongsProject:      demo-cf
 * BelongsPackage:      com.kxdkcf.mapper
 * Author:              wenhao
 * CreateTime:          2025-03-10  13:36
 * Description:         TODO
 * Version:             1.0
 */
@Mapper
public interface HealthMapper {
    void insertHealthData(HealthData healthData);

    @Delete("delete from health_analyse where user_id =#{userId}")
    void deleteByUserId(Long userId);

    @Select("select user_id, title, category, content, source, image, id from health_knowledge where user_id=#{userid}")
    List<HealthKnowledge> getByUserId(Long userId);

    @Select("select content  from health_analyse where user_id=#{userId} order by update_time desc limit 1")
    Map<String, String> getHealthAnalyse(Long userId);

    @AutoTime(OperationType.INSERT)
    void insertHealthRecommend(HealthRecommendation healthRecommendation);

    @Select("select content from health_recommendation where user_id=#{userId} order by update_time desc limit 1")
    Map<String, String> getHealthRecommendation(Long userId);
}
