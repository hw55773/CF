package com.kxdkcf.service.Impl;

import cn.hutool.core.bean.BeanUtil;
import com.kxdkcf.Result.Result;
import com.kxdkcf.ai.requset.RequestDTO;
import com.kxdkcf.ai.requset.ResponseFormat;
import com.kxdkcf.ai.requset.Tool;
import com.kxdkcf.ai.requset.WebSearch;
import com.kxdkcf.ai.response.RespondsDTO;
import com.kxdkcf.constant.ai.Module;
import com.kxdkcf.constant.ai.Role;
import com.kxdkcf.context.ThreadLocalValueUser;
import com.kxdkcf.enity.Dialog;
import com.kxdkcf.enity.HealthRecommendation;
import com.kxdkcf.enity.Message;
import com.kxdkcf.httputils.HttpClientUtil;
import com.kxdkcf.mapper.AiMapper;
import com.kxdkcf.mapper.HealthMapper;
import com.kxdkcf.properties.AiProperties;
import com.kxdkcf.service.IAiService;
import com.kxdkcf.vo.DialogVO;
import com.kxdkcf.vo.MessageVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * BelongsProject:      demo-cf
 * BelongsPackage:      com.kxdkcf.service.Impl
 * Author:              wenhao
 * CreateTime:          2025-03-01  15:03
 * Description:         TODO
 * Version:             1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AiService implements IAiService {

    private final AiMapper aiMapper;
    private final AiProperties aiProperties;
    private final HealthMapper healthMapper;
    private final StringRedisTemplate stringRedisTemplate;

    public Result addDislog() {

        Dialog dialog = new Dialog();

        dialog.setUserId(ThreadLocalValueUser.getThreadLocalValue(Long.class));

        dialog.setCreateTime(LocalDateTime.now());
        dialog.setUpdateTime(LocalDateTime.now());
        aiMapper.insert(dialog);

        DialogVO dialogVO = new DialogVO();
        dialogVO.setId(dialog.getId());

        return Result.success(dialogVO);
    }


    public void addMessage(String content, Long dialogId, byte type) {

        Message message = new Message();
        message.setContent(content);
        message.setType(type);
        message.setDialogId(dialogId);
        message.setCreateTime(LocalDateTime.now());
        aiMapper.insertMessage(message);

    }


    public List<MessageVO> showRecord(Long dialogId) {
        //根据会话id查询消息记录
        List<Message> messageList = aiMapper.selectByDialogId(dialogId);

        // 使用 Stream + Hutool 进行属性拷贝
        return messageList.stream()
                .map(message -> {
                    MessageVO vo = new MessageVO();
                    // 使用 hutool 拷贝同名属性（自动跳过不存在的字段）
                    BeanUtil.copyProperties(message, vo);
                    return vo;
                })
                .collect(Collectors.toList());
    }

    public Result showDialogs() {

        //从线程本地缓存里拿到用户id
        Long userId = ThreadLocalValueUser.getThreadLocalValue(Long.class);
        //根据用户id拿到对应的所有会话主题
        List<Dialog> dialogs = aiMapper.selectByUserId(userId);

        //返回所有记录
        return Result.success(dialogs);
    }


    @Transactional(rollbackFor = Exception.class)
    public Result deleteDialogById(Long dialogId) {

        //根据dialogId删除dialog表和message表数据
        //根据id删除dialog表数据
        aiMapper.deleteDialogById(dialogId);
        //根据dialogId删除message表数据
        aiMapper.deleteMessageByDialogId(dialogId);

        return Result.success();
    }


    public Result updateDialog(Dialog dialog) {

        dialog.setUpdateTime(LocalDateTime.now());
        aiMapper.updateDialog(dialog);


        return Result.success();
    }

    @Async("taskExecutor")
    public void healthRecommend(String string, String content, Long userId) {
        // 提取固定部分为常量
        final int TOP_K = 6;
        final double TEMPERATURE = 0.7;
        final int MAX_TOKENS = 8192;

        List<com.kxdkcf.ai.requset.Message> messages = buildMessages(string, content);
        List<Tool> tools = buildTools();

        ResponseFormat responseFormat = new ResponseFormat();
        responseFormat.setType("json_object");

        RequestDTO request = RequestDTO.builder()
                .max_tokens(MAX_TOKENS)
                .model(Module.Ultra_4.getValue())
                .response_format(responseFormat)
                .messages(messages)
                .top_k(TOP_K)
                .tools(tools)
                .temperature(TEMPERATURE)
                .stream(false)
                .build();

        RespondsDTO respondsDTO;
        try {
            respondsDTO = HttpClientUtil
                    .doPostAIJson(aiProperties.getUrl(), request, aiProperties.getApiKey(), RespondsDTO.class);
        } catch (IOException e) {
            log.error("AI请求失败，userId: {}", userId, e);
            throw new RuntimeException("AI请求失败", e);
        }

        if (respondsDTO == null || CollectionUtils.isEmpty(respondsDTO.getChoices())) {
            log.warn("AI响应为空，userId: {}", userId);
            return;
        }

        String resultContent = respondsDTO.getChoices().get(0).getMessage().getContent();
        HealthRecommendation healthRecommendation = new HealthRecommendation();
        healthRecommendation.setUserId(userId);
        healthRecommendation.setContent(resultContent);
        log.info(resultContent);

        try {
            healthMapper.insertHealthRecommend(healthRecommendation);
        } catch (Exception e) {
            log.error("插入健康推荐失败，userId: {}", userId, e);
        }

        try {
            stringRedisTemplate.delete("health_recommend:" + userId);
        } catch (Exception e) {
            log.warn("删除健康推荐缓存失败，userId: {}", userId, e);
        }
    }

    // 封装消息构建逻辑
    private List<com.kxdkcf.ai.requset.Message> buildMessages(String string, String content) {
        com.kxdkcf.ai.requset.Message message1 = com.kxdkcf.ai.requset.Message.builder()
                .role(Role.system.getValue())
                .content("你是一位健康推荐助手,目标任务,你将根据健康数据，分析健康状态,给出的水果、蔬菜、谷物等推荐")
                .build();
        com.kxdkcf.ai.requset.Message message2 = com.kxdkcf.ai.requset.Message.builder()
                .role(Role.user.getValue())
                .content("任务: 根据健康数据，分析健康状态，给出在水果、蔬菜、谷物等方面的推荐\n" +
                        "数据: 用户基本健康数据: " + string + "\n" +
                        "用户血液检测数据: " + content + "\n" +
                        "输出格式(json格式): {" +
                        "fruits: [" +
                        "{name: [水果名]" +
                        "detail: [这种水果对人体的益处]" +
                        "}," +
                        "....(10种水果推荐)" +
                        "]," +
                        "vegetables: [" +
                        "{name: [蔬菜名]" +
                        "detail: [这种蔬菜对人体的益处]" +
                        "}," +
                        "....(10种蔬菜推荐)" +
                        "]," +
                        "cereals: [" +
                        "{name: [谷物名]" +
                        "detail: [这种谷物对人体的益处]" +
                        "}," +
                        "....(10种谷物推荐)" +
                        "]" +
                        "}")
                .build();
        return Arrays.asList(message1, message2);
    }

    // 封装工具构建逻辑
    private List<Tool> buildTools() {
        Tool tool = new Tool();
        tool.setType("web_search");
        WebSearch web = new WebSearch();
        web.setEnable(true);
        web.setSearch_mode("deep");
        tool.setWeb_search(web);
        return Collections.singletonList(tool);
    }

}
