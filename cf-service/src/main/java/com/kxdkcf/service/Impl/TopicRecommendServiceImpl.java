package com.kxdkcf.service.Impl;

import cn.hutool.core.util.BooleanUtil;
import com.kxdkcf.Result.Result;
import com.kxdkcf.context.ThreadLocalValueUser;
import com.kxdkcf.dto.RelateDTO;
import com.kxdkcf.mapper.RelateMapper;
import com.kxdkcf.mapper.TopicMapper;
import com.kxdkcf.service.IRecommendService;
import com.kxdkcf.utils.cf.ItemCF;
import com.kxdkcf.vo.TopicVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * BelongsProject:      demo-cf
 * BelongsPackage:      com.kxdkcf.service.Impl
 * Author:              wenhao
 * CreateTime:          2024-12-27  19:30
 * Description:         TODO
 * Version:             1.0
 */
@RequiredArgsConstructor
@Service
public class TopicRecommendServiceImpl implements IRecommendService {

    private final RelateMapper relateMapper;
    private final TopicMapper topicMapper;


    @Override
    public Result quryByTopicId(Long topicId) {

        Long userId = ThreadLocalValueUser.getThreadLocalValue(Long.class);
        List<RelateDTO> relateList = relateMapper.getRelateList(null);
        List<Long> recommend = ItemCF.recommend(topicId, relateList, 10);
        List<TopicVO> topicList = topicMapper.getTopicList(recommend, null, null, null);
        topicList.forEach(topicVO -> {
            //根据topicI和userId查询是否点赞和收藏
            Long topicVOId = topicVO.getId();
            Map<String, Boolean> map = topicMapper.selectIsLike(topicVOId, userId);
            if (BooleanUtil.isTrue(map.get("is_liked"))) {
                topicVO.setIsLiked(Boolean.TRUE);
            } else {
                topicVO.setIsLiked(Boolean.FALSE);
            }
            if (BooleanUtil.isTrue(map.get("is_collected"))) {
                topicVO.setIsCollected(Boolean.TRUE);
            } else {
                topicVO.setIsCollected(Boolean.FALSE);
            }
        });

        return Result.success(topicList);
    }
}
