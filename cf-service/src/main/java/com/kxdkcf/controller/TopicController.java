package com.kxdkcf.controller;

import cn.hutool.core.bean.BeanUtil;
import com.kxdkcf.Result.Result;
import com.kxdkcf.context.ThreadLocalValueUser;
import com.kxdkcf.dto.PageTopicDTO;
import com.kxdkcf.dto.ReplyDTO;
import com.kxdkcf.dto.TopicDTO;
import com.kxdkcf.enity.Topic;
import com.kxdkcf.service.IRecommendService;
import com.kxdkcf.service.ITopicService;
import com.kxdkcf.vo.PageTopicBean;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * BelongsProject:      demo-cf
 * BelongsPackage:      com.kxdkcf.controller
 * Author:              wenhao
 * CreateTime:          2024-12-28  13:38
 * Description:         TODO
 * Version:             1.0
 */
@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/topic")
@Tag(name = "主题相关接口")
public class TopicController {

    private final IRecommendService userRecommendServiceImpl;
    private final IRecommendService topicRecommendServiceImpl;
    private final ITopicService topicServiceImpl;

    /**
     * 根据用户id获取推荐主题
     *
     * @param pageTopicDTO 分页查询传输对象
     * @return 主题信息（分页）
     */
    @Operation(summary = "根据用户id获取推荐主题")
    @GetMapping("/recommend")
    public Result queryTopic(@RequestParam(required = false) PageTopicDTO pageTopicDTO) {

        if (pageTopicDTO == null) {
            pageTopicDTO = new PageTopicDTO();
        }
        pageTopicDTO.setUserId(ThreadLocalValueUser.getThreadLocalValue(Long.class));
        return userRecommendServiceImpl.queryTopic(pageTopicDTO);
    }

    /**
     * 根据主题id获取推荐主题
     *
     * @param topicId 主题id
     * @return 主题推荐信息
     */
    @Operation(summary = "根据主题id获取推荐主题")
    @GetMapping("/recommend/{id}")
    public Result queryByTopicId(@PathVariable("id") Long topicId) {

        return topicRecommendServiceImpl.quryByTopicId(topicId);
    }

    /**
     * 添加帖子
     *
     * @param topicDTO 接受对象
     * @return 统一结果集
     */
    @Operation(summary = "添加帖子")
    @PostMapping("/add")
    public Result addTopic(@RequestBody TopicDTO topicDTO) {
        Topic topic = new Topic();
        BeanUtil.copyProperties(topicDTO, topic);
        return topicServiceImpl.add(topic);
    }

    /**
     * 批量更新主题
     *
     * @param topicDTOs 批量主题
     * @return 更新成功信息
     */
    @Operation(summary = "批量更新主题")
    @PostMapping("/update")
    public Result updateTopic(@RequestBody List<TopicDTO> topicDTOs) {

        log.info("topicDTOs:{}", topicDTOs);
        return topicServiceImpl.updateById(topicDTOs);
    }

    /**
     * 获取主题详情
     *
     * @param topicId 主题id
     * @return 主题详情
     */
    @Operation(summary = "获取主题详情")
    @GetMapping("/topicDetails/{topicId}")
    public Result queryTopicDetails(@PathVariable("topicId") Long topicId) {

        return topicServiceImpl.queryTopicDetails(topicId);

    }

    /**
     * 提交回复
     *
     * @param reply 回复内容传输实体
     * @return 插入的回复实体
     */
    @Operation(summary = "提交回复")
    @PostMapping("/postReply")
    public Result postReply(@RequestBody ReplyDTO reply) {

        return topicServiceImpl.postReply(reply);
    }

    /**
     * 更新回复记录状态
     *
     * @param id 回复记录id
     * @return 成功信息
     */
    @Operation(summary = "更新回复记录状态")
    @PutMapping("/updateReply/{id}")
    public ResponseEntity<?> updateReply(@PathVariable Long id) {

        return topicServiceImpl.updateReply(id);
    }

    /**
     * 删除回复记录
     *
     * @param replyId 回复id
     * @return 成功信息
     */
    @Operation(summary = "删除回复记录")
    @DeleteMapping("/deleteReply/{replyId}")
    public ResponseEntity<?> deleteReplyById(@PathVariable Long replyId) {


        return topicServiceImpl.deleteReplyById(replyId);
    }


    /**
     * 获取收藏主题
     *
     * @param pageTopicDTO 分页查询DTO
     * @return 成功信息
     */
    @Operation(summary = "获取收藏主题")
    @GetMapping("/query-collected")
    public ResponseEntity<?> queryCollected(PageTopicDTO pageTopicDTO) {

        PageTopicBean pageTopicBean = topicServiceImpl.queryCollected(pageTopicDTO);

        return ResponseEntity.ok(Result.success(pageTopicBean));

    }

}
