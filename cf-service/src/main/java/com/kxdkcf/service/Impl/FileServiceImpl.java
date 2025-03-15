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


        RespondsDTO respondsDTO = null;
        StringBuffer content = new StringBuffer();
        String string = healthDataDTO.toString();
        List<MultipartFile> files = healthDataDTO.getFiles();
        files.forEach(file -> {
            try (InputStream inputStream = file.getInputStream()) {
                content.append(OcrUtils.recognizeImageText(ocrClient, inputStream));
                content.append("\n");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        //获取ai回复
        List<Message> messages = new ArrayList<>();
        Message message1 = Message.builder()
                .role(Role.system.getValue())
                .content("你是一位健康助手,目标任务,你将根据健康数据，给出健康状态分析,并按照:饮食建议、运动健身建议、生活作息建议;这三部分输出内容")
                .build();
        Message message = Message.builder()
                .role(Role.user.getValue())
                .content("任务: 分析以下数据，给出该用户的健康状态与建议" + "\n" + " 用户健康基础数据:\n" + string + "用户血液检测数据:\n" + content
                        + "按下面三部分[dietaryAdvice: [饮食建议]" + "\n" + "exerciseAndFitnessAdvice: [运动健身建议]" + "\n" + "adviceOnDailyRoutine: [生活作息建议] ]" + "\n" + "控制输出格式 (Json格式,不要出现用户的姓名)").build();
        messages.add(message1);
        messages.add(message);
        ResponseFormat responseFormat = new ResponseFormat();
        responseFormat.setType("json_object");
        List<Tool> tools = new ArrayList<>();
        Tool tool = new Tool();
        tool.setType("web_search");
        WebSearch web = new WebSearch();
        web.setEnable(true);
        web.setSearch_mode("deep");
        tool.setWeb_search(web);
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
        //插入更新健康推荐信息
        aiService.healthRecommend(string, content.toString(), userId);
        try {
            System.out.println(message.getContent());
            respondsDTO = HttpClientUtil
                    .doPostAIJson(aiProperties.getUrl(), request, aiProperties.getApiKey(), RespondsDTO.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        System.out.println(respondsDTO.toString());
        HealthData healthData = new HealthData();
        healthData.setCreateTime(LocalDateTime.now());
        healthData.setUpdateTime(LocalDateTime.now());
        healthData.setUserId(userId);
        healthData.setContent(respondsDTO.getChoices().get(0).getMessage().getContent());
        healthMapper.insertHealthData(healthData);
        stringRedisTemplate.delete("health_analyse:" + userId);
    }
}
