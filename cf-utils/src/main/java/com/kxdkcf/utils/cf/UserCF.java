package com.kxdkcf.utils.cf;

import com.kxdkcf.dto.RelateDTO;

import java.util.*;
import java.util.stream.Collectors;

public class UserCF {

    public static List<Long> recommend(Long userId, Map<Long, List<RelateDTO>> userItemRatingsMap, Integer k) {
        // 参数校验
        if (userId == null || userItemRatingsMap == null || k == null || k <= 0) {
            return Collections.emptyList();
        }
        // 获取目标用户数据
        List<RelateDTO> userRatings = userItemRatingsMap.getOrDefault(userId, Collections.emptyList());
        Set<Long> ratedItems = userRatings.stream()
                .map(RelateDTO::getTopicId)
                .collect(Collectors.toSet());
        // 计算物品流行度
        Map<Long, Integer> itemPopularity = computeItemPopularity(userItemRatingsMap);
        // 计算相似用户
        Map<Long, Double> similarityMap = CoreMath.computeNeighbor(userId, userItemRatingsMap, 0);
        List<Map.Entry<Long, Double>> sortedSimilarity = similarityMap.entrySet().stream()
                .filter(entry -> entry.getValue() > 0)  // 排除负相关用户
                .sorted(Map.Entry.<Long, Double>comparingByValue().reversed())
                .limit(k)
                .toList();
        // 计算物品加权得分
        Map<Long, Double> itemScoreMap = new HashMap<>();
        sortedSimilarity.forEach(entry -> {
            Long similarUserId = entry.getKey();
            double similarity = entry.getValue();
            userItemRatingsMap.getOrDefault(similarUserId, Collections.emptyList())
                    .forEach(r -> {
                        if (!ratedItems.contains(r.getTopicId())) {
                            double popularityPenalty = Math.log(itemPopularity.getOrDefault(r.getTopicId(), 1) + 1);
                            double weightedScore = (r.getIndex() * similarity) / popularityPenalty;
                            itemScoreMap.merge(r.getTopicId(), weightedScore, Double::sum);
                        }
                    });
        });
        // 生成推荐列表（带冷启动处理）
        List<Long> recommendations = itemScoreMap.entrySet().stream()
                .sorted(Map.Entry.<Long, Double>comparingByValue().reversed())
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
        // 冷启动处理：当无推荐结果时返回热门物品
        if (recommendations.isEmpty()) {
            return getPopularItems(itemPopularity, 10);
        }
        return recommendations;
    }

    private static Map<Long, Integer> computeItemPopularity(Map<Long, List<RelateDTO>> userItemRatingsMap) {
        Map<Long, Integer> popularityMap = new HashMap<>();
        userItemRatingsMap.values().forEach(list ->
                list.forEach(dto ->
                        popularityMap.merge(dto.getTopicId(), 1, Integer::sum)
                )
        );
        return popularityMap;
    }

    private static List<Long> getPopularItems(Map<Long, Integer> itemPopularity, int limit) {
        return itemPopularity.entrySet().stream()
                .sorted(Map.Entry.<Long, Integer>comparingByValue().reversed())
                .limit(limit)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }
}