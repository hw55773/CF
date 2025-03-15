package com.kxdkcf.service.Impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kxdkcf.Result.Result;
import com.kxdkcf.context.ThreadLocalValueUser;
import com.kxdkcf.enity.FoodData;
import com.kxdkcf.enity.HealthAdvice;
import com.kxdkcf.enity.HealthKnowledge;
import com.kxdkcf.mapper.HealthMapper;
import com.kxdkcf.service.IHealthService;
import com.kxdkcf.vo.HealthKnowledgeVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * BelongsProject:      demo-cf
 * BelongsPackage:      com.kxdkcf.service.Impl
 * Author:              wenhao
 * CreateTime:          2025-03-10  13:35
 * Description:         TODO
 * Version:             1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class HealthServiceImpl implements IHealthService {

    private final HealthMapper healthMapper;
    private final ObjectMapper objectMapper;
    private final StringRedisTemplate stringRedisTemplate;

    public ResponseEntity<?> getHealthKnowledge() {
        Long userId = ThreadLocalValueUser.getThreadLocalValue(Long.class);
        List<HealthKnowledge> healthKnowledgeList = healthMapper.getByUserId(userId);
        List<HealthKnowledgeVO> healthKnowledgeVOList = healthKnowledgeList.stream().map(healthKnowledge -> {
            HealthKnowledgeVO healthKnowledgeVO = new HealthKnowledgeVO();
            healthKnowledgeVO.setTitle(healthKnowledge.getTitle());
            healthKnowledgeVO.setContent(healthKnowledge.getContent());
            healthKnowledgeVO.setImage(healthKnowledge.getImage());
            healthKnowledgeVO.setSource(healthKnowledge.getSource());
            if (healthKnowledge.getCategory() == 0) {
                healthKnowledgeVO.setCategory("饮食健康");
            } else if (healthKnowledge.getCategory() == 1) {
                healthKnowledgeVO.setCategory("运动健身");
            } else if (healthKnowledge.getCategory() == 2) {
                healthKnowledgeVO.setCategory("心理健康");
            }
            return healthKnowledgeVO;
        }).collect(Collectors.toList());


        return ResponseEntity.ok(Result.success(healthKnowledgeVOList));
    }


    public ResponseEntity<?> getHealthAnalyse(Long userId) {
        HealthAdvice healthAdvice = null;
        //查查缓存有没有
        String s = stringRedisTemplate.opsForValue().get("health_analyse:" + userId);
        if (s != null && !s.trim().equals("")) {
            try {
                healthAdvice = objectMapper.readValue(s, HealthAdvice.class);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        } else {
            Map<String, String> map = healthMapper.getHealthAnalyse(userId);
            if (map == null) {
                return ResponseEntity.ok(Result.success());
            }
            String json = map.get("content");
            log.info(json);
            String cleanJson = json.replaceAll("^```json", "")
                    .replaceAll("```$", "")
                    .trim();
            log.info(cleanJson);

            try {
                healthAdvice = objectMapper.readValue(cleanJson, HealthAdvice.class);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
            stringRedisTemplate.opsForValue().set("health_analyse:" + userId, cleanJson, 2, TimeUnit.DAYS);
        }
        return ResponseEntity.ok(Result.success(healthAdvice));
    }

    /**
     * 获取健康推荐信息
     *
     * @param userId 用户id
     * @return 推荐信息
     */
    public ResponseEntity<?> getHealthRecommendations(Long userId) {
        FoodData foodData = null;
        //先去查查缓存
        String s1 = stringRedisTemplate.opsForValue().get("health_recommend:" + userId);
        if (s1 != null && !s1.trim().equals("")) {
            try {
                foodData = objectMapper.readValue(s1, FoodData.class);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        } else {
            Map<String, String> map = healthMapper.getHealthRecommendation(userId);
            if (map == null) {
                return ResponseEntity.ok(Result.success());
            }
            String json = map.get("content");
            log.info(json);
            String cleanJson = json.replaceAll("^```json", "")
                    .replaceAll("```$", "")
                    .trim();
            log.info(cleanJson);

            try {
                foodData = objectMapper.readValue(cleanJson, FoodData.class);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
            stringRedisTemplate.opsForValue().set("health_recommend:" + userId, cleanJson, 2, TimeUnit.DAYS);
        }
        return ResponseEntity.ok(Result.success(foodData));
    }
}
