package com.kxdkcf.service.Impl;

import cn.hutool.core.util.BooleanUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.kxdkcf.Result.Result;
import com.kxdkcf.dto.PageTopicDTO;
import com.kxdkcf.dto.RelateDTO;
import com.kxdkcf.mapper.RelateMapper;
import com.kxdkcf.mapper.TopicMapper;
import com.kxdkcf.service.IRecommendService;
import com.kxdkcf.utils.cf.UserCF;
import com.kxdkcf.vo.PageTopicBean;
import com.kxdkcf.vo.TopicVO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

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
public class UserRecommendServiceImpl implements IRecommendService {
    private final RelateMapper relateMapper;
    private final TopicMapper topicMapper;
    private final StringRedisTemplate stringRedisTemplate;
    private final ObjectMapper objectMapper;


    public Result queryTopic(PageTopicDTO pageTopicDTO) {
        List<Long> list;
        try {
            //查询用户主题关系表中有哪些用户
            //查查缓存里有没有
            String s = stringRedisTemplate.opsForValue().get("relate_user_id");
            if (s != null && !s.trim().equals("")) {
                list = objectMapper.readValue(s, new TypeReference<>() {
                });
            } else {

                List<Long> userIdList = relateMapper.getUsers();
                list = userIdList.stream().distinct().toList();
                //添加缓存
                stringRedisTemplate.opsForValue().set("relate_user_id", objectMapper.writeValueAsString(list), 6, TimeUnit.HOURS);
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        Map<Long, List<RelateDTO>> map = new HashMap<>();
        list.forEach(id -> {
            //根据用户id查询出对应评分关系列表
            map.put(id, relateMapper.getRelateList(id));

        });
        List<Long> recommend = UserCF.recommend(pageTopicDTO.getUserId(), map, 10).stream().distinct().toList();

        if (pageTopicDTO.getPage() == null || pageTopicDTO.getPageSize() == null) {
            pageTopicDTO.setPageSize(10);
            pageTopicDTO.setPage(1);
        }

        PageHelper.startPage(pageTopicDTO.getPage(), pageTopicDTO.getPageSize());
        //根据一系列topicId分页查询topic列表
        List<TopicVO> topicVOList = topicMapper.getTopicList(recommend, pageTopicDTO.getName(), pageTopicDTO.getBegin(), pageTopicDTO.getEnd());

        //补充数据
        topicVOList.forEach(topicVO -> {
            //根据topicI和userId查询是否点赞和收藏
            Long userId = pageTopicDTO.getUserId();
            Long topicId = topicVO.getId();
            Map<String, Boolean> map1 = topicMapper.selectIsLike(topicId, userId);
            if (map1 == null) {
                topicVO.setIsCollected(false);
                topicVO.setIsLiked(false);
            } else {
                if (BooleanUtil.isTrue(map1.get("is_liked"))) {
                    topicVO.setIsLiked(Boolean.TRUE);
                } else {

                    topicVO.setIsLiked(Boolean.FALSE);
                }
                if (BooleanUtil.isTrue(map1.get("is_collected"))) {
                    topicVO.setIsCollected(Boolean.TRUE);
                } else {
                    topicVO.setIsCollected(Boolean.FALSE);
                }
            }
        });

        Page<TopicVO> page = (Page<TopicVO>) topicVOList;
        PageTopicBean pageTopicBean = new PageTopicBean(page.getTotal(), page.getResult());
        return Result.success(pageTopicBean);
    }

}
