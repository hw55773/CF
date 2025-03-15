package com.kxdkcf.utils.cf;

import com.kxdkcf.dto.RelateDTO;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 基于主题的推荐
 */
public class ItemCF {
    public static List<Long> recommend(Long itemId, List<RelateDTO> list, int topN) {
        Map<Long, List<RelateDTO>> itemMap = list.stream().collect(Collectors.groupingBy(RelateDTO::getTopicId));
        Map<Long, Double> itemDisMap = CoreMath.computeNeighbor(itemId, itemMap, 1);
        // 过滤负相关并排序
        return itemDisMap.entrySet().stream()
                .filter(e -> e.getValue() > 0) // 过滤负相关
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .limit(topN)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }
}