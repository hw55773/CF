package com.kxdkcf.service.Impl;

import com.kxdkcf.ai.requset.*;
import com.kxdkcf.ai.response.RespondsDTO;
import com.kxdkcf.constant.ai.Module;
import com.kxdkcf.constant.ai.Role;
import com.kxdkcf.dto.HealthDataDTO;
import com.kxdkcf.enity.HealthData;
import com.kxdkcf.httputils.HttpClientUtil;
import com.kxdkcf.mapper.HealthMapper;
import com.kxdkcf.properties.AiProperties;
import com.kxdkcf.service.IFileService;
import com.kxdkcf.utils.aliyun.OcrClient;
import com.kxdkcf.utils.aliyun.OcrUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * BelongsProject:      demo-cf
 * BelongsPackage:      com.kxdkcf.service.Impl
 * Author:              wenhao
 * CreateTime:          2025-03-16  16:51
 * Description:         TODO
 * Version:             1.0
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class FileServiceImpl implements IFileService {

    private final HealthMapper healthMapper;
    private final AiProperties aiProperties;
    private final OcrClient ocrClient;
    private final StringRedisTemplate stringRedisTemplate;
    private final AiService aiService;

    @Async("taskExecutor")
    public void processingOfHealthData(HealthDataDTO healthDataDTO, Long userId) {
        if (healthDataDTO == null) {
            log.warn("HealthDataDTO is null for user: {}", userId);
            return;
        }

        RespondsDTO respondsDTO = null;
        StringBuilder content = new StringBuilder();
        String string = healthDataDTO.toString();
        List<MultipartFile> files = healthDataDTO.getFiles();

        if (files != null && !files.isEmpty()) {
            files.forEach(file -> {
                if (file.isEmpty()) {
                    log.warn("Empty file found for user: {}", userId);
                    return;
                }
                try (InputStream inputStream = file.getInputStream()) {
                    String ocrResult = OcrUtils.recognizeImageText(ocrClient, inputStream);
                    if (ocrResult != null && !ocrResult.trim().isEmpty()) {
                        content.append(ocrResult).append("\n");
                    }
                } catch (Exception e) {
                    log.error("Error reading file for user {}: {}", userId, e.getMessage(), e);
                }
            });
        }

        // 构建消息体
        List<Message> messages = new ArrayList<>();
        messages.add(Message.builder()
                .role(Role.system.getValue())
                .content("你是一位健康助手,目标任务,你将根据健康数据，给出健康状态分析,并按照:饮食建议、运动健身建议、生活作息建议;这三部分输出内容")
                .build());
        messages.add(Message.builder()
                .role(Role.user.getValue())
                .content("任务: 分析以下数据，给出该用户的健康状态与建议" + "\n" +
                        " 用户健康基础数据:\n" + string +
                        "用户血液检测数据:\n" + content +
                        "[输出格式要求]:\n" +
                        "dietaryAdvice: [饮食建议]\n" +
                        "exerciseAndFitnessAdvice: [运动健身建议]\n" +
                        "adviceOnDailyRoutine: [生活作息建议]\n" +
                        "请以 JSON 格式输出，不要包含用户姓名")
                .build());

        ResponseFormat responseFormat = new ResponseFormat();
        responseFormat.setType("json_object");

        WebSearch web = new WebSearch();
        web.setEnable(true);
        web.setSearch_mode("deep");

        Tool tool = new Tool();
        tool.setType("web_search");
        tool.setWeb_search(web);

        List<Tool> tools = new ArrayList<>();
        tools.add(tool);

        RequestDTO request = RequestDTO.builder()
                .max_tokens(4096)
                .model(Module.Ultra_4.getValue())
                .response_format(responseFormat)
                .messages(messages)
                .top_k(5)
                .tools(tools)
                .temperature(0.5)
                .stream(false)
                .build();

        aiService.healthRecommend(string, content.toString(), userId);

        try {
            log.info("Sending AI request for user: {}", userId);
            respondsDTO = HttpClientUtil
                    .doPostAIJson(aiProperties.getUrl(), request, aiProperties.getApiKey(), RespondsDTO.class);
        } catch (IOException e) {
            log.error("AI request failed for user {}: {}", userId, e.getMessage(), e);
            return;
        }

        if (respondsDTO == null || CollectionUtils.isEmpty(respondsDTO.getChoices())) {
            log.warn("Invalid AI response for user: {}", userId);
            return;
        }

        String aiContent = respondsDTO.getChoices().get(0).getMessage().getContent();
        if (aiContent == null || aiContent.trim().isEmpty()) {
            log.warn("Empty AI content returned for user: {}", userId);
            return;
        }

        log.info("Received AI response: {}", aiContent);

        HealthData healthData = new HealthData();
        healthData.setCreateTime(LocalDateTime.now());
        healthData.setUpdateTime(LocalDateTime.now());
        healthData.setUserId(userId);
        healthData.setContent(aiContent);

        healthMapper.insertHealthData(healthData);
        stringRedisTemplate.delete("health_analyse:" + userId);
    }

}
