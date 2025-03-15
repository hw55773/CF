package com.kxdkcf.utils.cf;

import com.kxdkcf.dto.RelateDTO;

import java.util.*;
import java.util.stream.Collectors;


public class CoreMath {
    private static final int MIN_COMMON_ITEMS = 2; // 最小共同评价物品数

    public static Map<Long, Double> computeNeighbor(Long key, Map<Long, List<RelateDTO>> map, int type) {
        Map<Long, Double> distMap = new HashMap<>();
        List<RelateDTO> userItems = map.getOrDefault(key, Collections.emptyList());

        // 转换为快速查找的数据结构
        Map<Long, Double> targetMap = convertToMap(userItems, type);

        map.keySet().parallelStream()
                .filter(k -> !k.equals(key))
                .forEach(otherUserId -> {
                    List<RelateDTO> otherUserItems = map.get(otherUserId);
                    Map<Long, Double> otherMap = convertToMap(otherUserItems, type);
                    double similarity = calculateSimilarity(targetMap, otherMap, type);
                    if (similarity > 0) {  // 只保留正相关
                        synchronized (distMap) {
                            distMap.put(otherUserId, similarity);
                        }
                    }
                });

        return distMap;
    }

    private static Map<Long, Double> convertToMap(List<RelateDTO> list, int type) {
        return list.stream().collect(Collectors.toMap(
                dto -> type == 0 ? dto.getTopicId() : dto.getUserId(),
                RelateDTO::getIndex,
                (v1, v2) -> v1  // 重复键处理
        ));
    }

    private static double calculateSimilarity(Map<Long, Double> targetMap,
                                              Map<Long, Double> otherMap,
                                              int type) {
        Set<Long> commonKeys = new HashSet<>(targetMap.keySet());
        commonKeys.retainAll(otherMap.keySet());

        if (commonKeys.size() < MIN_COMMON_ITEMS) {
            return 0.0;
        }

        List<Double> targetScores = new ArrayList<>();
        List<Double> otherScores = new ArrayList<>();
        for (Long key : commonKeys) {
            targetScores.add(targetMap.get(key));
            otherScores.add(otherMap.get(key));
        }

        return pearsonCorrelation(targetScores, otherScores);
    }

    private static double pearsonCorrelation(List<Double> xs, List<Double> ys) {
        int n = xs.size();
        double sumX = 0.0, sumY = 0.0;
        double sumXSq = 0.0, sumYSq = 0.0;
        double pSum = 0.0;

        for (int i = 0; i < n; i++) {
            double x = xs.get(i);
            double y = ys.get(i);

            sumX += x;
            sumY += y;
            sumXSq += x * x;
            sumYSq += y * y;
            pSum += x * y;
        }

        double numerator = pSum - (sumX * sumY / n);
        double denominator = Math.sqrt((sumXSq - sumX * sumX / n) * (sumYSq - sumY * sumY / n));

        return denominator == 0 ? 0 : numerator / denominator;
    }
}