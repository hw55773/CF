package com.kxdkcf.service.Impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.BooleanUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.kxdkcf.Result.Result;
import com.kxdkcf.annotation.AutoTime;
import com.kxdkcf.constant.OperationType;
import com.kxdkcf.context.ThreadLocalValueUser;
import com.kxdkcf.dto.PageTopicDTO;
import com.kxdkcf.dto.ReplyDTO;
import com.kxdkcf.dto.TopicDTO;
import com.kxdkcf.enity.Reply;
import com.kxdkcf.enity.Topic;
import com.kxdkcf.enity.User;
import com.kxdkcf.mapper.*;
import com.kxdkcf.service.ITopicService;
import com.kxdkcf.vo.PageTopicBean;
import com.kxdkcf.vo.ReplyVO;
import com.kxdkcf.vo.TopicDetailVO;
import com.kxdkcf.vo.TopicVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * BelongsProject:      demo-cf
 * BelongsPackage:      com.kxdkcf.service.Impl
 * Author:              wenhao
 * CreateTime:          2025-01-18  15:01
 * Description:         TODO
 * Version:             1.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TopicServiceImpl implements ITopicService {

    private final TopicMapper topicMapper;
    private final LikeMapper likeMapper;
    private final ReplyMapper replyMapper;
    private final RelateMapper relateMapper;
    private final UserMapper userMapper;
    private final StringRedisTemplate stringRedisTemplate;
    private final ObjectMapper objectMapper;

    @AutoTime(OperationType.INSERT)
    @Transactional(rollbackFor = Exception.class)
    public Result add(Topic topic) {
        topic.setUserId(ThreadLocalValueUser.getThreadLocalValue(Long.class));
        topicMapper.insert(topic);
        //查询所有用户
        List<User> users = null;
        //先查查缓存里有没有
        String s1 = stringRedisTemplate.opsForValue().get("user_all");
        if (s1 != null && !s1.trim().equals("")) {
            try {
                users = objectMapper.readValue(s1, new TypeReference<>() {
                });
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        } else {
            users = userMapper.selectAllUser();
            //存入缓存
            String s = null;
            try {
                s = objectMapper.writeValueAsString(users);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
            stringRedisTemplate.opsForValue().set("user_all", s, 10, TimeUnit.DAYS);
        }
        List<Long> list = users.stream().map(User::getId).collect(Collectors.toList());
        //插入数据到relate表
        relateMapper.insertByTopicId(topic.getId(), list);


        return Result.success();
    }

    public Result updateById(List<TopicDTO> topicDTOs) {
        Long userId = ThreadLocalValueUser.getThreadLocalValue(Long.class);
        List<Topic> list = topicDTOs.stream().map(topicDTO -> {
            Topic topic = new Topic();
            BeanUtil.copyProperties(topicDTO, topic);
            topic.setUpdateTime(LocalDateTime.now());
            return topic;
        }).toList();
        //批量更新
        topicMapper.update(list);
        //更新点赞和收藏表

        topicDTOs.forEach(topicDTO -> {

            if (topicDTO.getIsCollected() == null) {
                topicDTO.setIsCollected(false);
            }
            if (topicDTO.getIsLiked() == null) {
                topicDTO.setIsLiked(false);
            }
            //先判断是否有记录
            Integer count = likeMapper.selectByUserIdAndTopicId(userId, topicDTO.getId());
            if (count == null || count == 0) {
                likeMapper.insert(userId, topicDTO.getId(), topicDTO.getIsLiked(), topicDTO.getIsCollected());
            } else if (count > 0) {
                likeMapper.update(userId, topicDTO.getId(), topicDTO.getIsLiked(), topicDTO.getIsCollected());
            }

        });


        return Result.success();
    }

    public Result queryTopicDetails(Long topicId) {

        //当前用户id
        Long userId = ThreadLocalValueUser.getThreadLocalValue(Long.class);
        //根据id查询主题详情
        TopicDetailVO topicVO = topicMapper.selectTopicDetails(topicId);


        if (topicVO == null) {
            return Result.error("不存在的主题");
        }
        //根据topicI和userId查询是否点赞和收藏
        Map<String, Boolean> map = topicMapper.selectIsLike(topicVO.getId(), userId);
        if (map == null) {
            topicVO.setIsCollected(false);
            topicVO.setIsLiked(false);
        } else {
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
        }
        List<ReplyVO> replyList = topicVO.getReplyList();
        replyList.forEach(replyVO -> {
            //根据当前用户设置每条回复的点赞状态
            Boolean liked = replyMapper.isLiked(userId, replyVO.getId());
            replyVO.setIsLiked(BooleanUtil.isTrue(liked));
        });
        topicVO.setReplyList(replyList);
        return Result.success(topicVO);

    }

   @Transactional(rollbackFor = Exception.class)
public Result postReply(ReplyDTO replyDTO) {
    // 校验 topicId 是否存在
    Topic topic = topicMapper.selectTopicById(replyDTO.getTopicId());
    if (topic == null) {
        return Result.error("话题不存在");
    }

    // 获取用户ID
    Long userId = ThreadLocalValueUser.getThreadLocalValue(Long.class);
    if (userId == null) {
        return Result.error("用户未登录");
    }

    // 构建回复对象
    Reply reply = new Reply();
    BeanUtil.copyProperties(replyDTO, reply);
    reply.setUserId(userId);
    reply.setCreateTime(LocalDateTime.now());
    reply.setIsLiked(false);
    reply.setLikeCount(0L);

    // 插入回复
    replyMapper.insert(reply);

    // 确保 reply 已插入并获取主键
    ReplyVO replyVO = new ReplyVO();
    BeanUtil.copyProperties(reply, replyVO);

    // 更新话题回复数
    topic.setReplyCount(topic.getReplyCount() + 1);
       List<Topic> list = new ArrayList<>();
       topicMapper.update(list);

    return Result.success(replyVO);
}


    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity<?> updateReply(Long replyId) {

        //当前用户
        Long userId = ThreadLocalValueUser.getThreadLocalValue(Long.class);
        Boolean liked = replyMapper.isLiked(userId, replyId);
        //根据id查询回复记录
        Reply reply = replyMapper.selectById(replyId);
        //更新消息状态
        if (BooleanUtil.isTrue(liked)) {
            reply.setLikeCount(reply.getLikeCount() - 1);
            replyMapper.update(reply);
            replyMapper.updateLiked(userId, replyId, BooleanUtil.isFalse(liked));
        } else if (BooleanUtil.isFalse(liked)) {
            reply.setLikeCount(reply.getLikeCount() + 1);
            replyMapper.update(reply);
            replyMapper.updateLiked(userId, replyId, true);
        } else {
            reply.setLikeCount(reply.getLikeCount() + 1);
            replyMapper.update(reply);
            replyMapper.insertReplyLiked(userId, replyId, true);
        }

        return ResponseEntity.ok(Result.success());
    }


    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity<?> deleteReplyById(Long replyId) {
        //找出所属主题
        Reply reply = replyMapper.selectById(replyId);
        //根据id删除回复记录
        replyMapper.deleteByReplyId(replyId);
        //删除回复记录状态
        replyMapper.deleteReplyLikedByReplyId(replyId);
        //更新主题回复数

        Long topicId = reply.getTopicId();
        //根据主题id查找对应主题
        Topic topic = topicMapper.selectTopicById(topicId);
        topic.setReplyCount(topic.getReplyCount() - 1);
        //更新主题信息
        topic.setUpdateTime(LocalDateTime.now());
        topicMapper.update(Collections.singletonList(topic));

        return ResponseEntity.ok(Result.success());
    }


    public PageTopicBean queryCollected(PageTopicDTO pageTopicDTO) {
        Long userId = ThreadLocalValueUser.getThreadLocalValue(Long.class);
        PageHelper.startPage(pageTopicDTO.getPage(), pageTopicDTO.getPageSize());
        //根据用户id查询收藏主题
        List<TopicVO> topicVOList = likeMapper.selectCollectedByUserId(userId);
        Page<TopicVO> page = (Page<TopicVO>) topicVOList;

        return new PageTopicBean(page.getTotal(), page.getResult());
    }
}
