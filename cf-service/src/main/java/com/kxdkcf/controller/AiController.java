package com.kxdkcf.controller;

import com.kxdkcf.Result.Result;
import com.kxdkcf.ai.requset.Message;
import com.kxdkcf.ai.requset.RequestDTO;
import com.kxdkcf.ai.requset.Tool;
import com.kxdkcf.ai.requset.WebSearch;
import com.kxdkcf.constant.ai.Module;
import com.kxdkcf.constant.ai.Role;
import com.kxdkcf.enity.Dialog;
import com.kxdkcf.httputils.HttpClientUtil;
import com.kxdkcf.properties.AiProperties;
import com.kxdkcf.service.IAiService;
import com.kxdkcf.vo.MessageVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * BelongsProject:      demo-cf
 * BelongsPackage:      com.kxdkcf.controller
 * Author:              wenhao
 * CreateTime:          2025-01-01  17:19
 * Description:         TODO
 * Version:             1.0
 */
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/ai")
@Tag(name = "Ai相关接口")
public class AiController {

    private final AiProperties aiProperties;
    private final IAiService aiService;

    /**
     * 根据用户发送内容Ai回应对应的回答
     *
     * @param content 用户内容
     * @return SSE流式返回
     */
    //@PostMapping("/chat")
    @Operation(summary = "根据用户发送内容Ai回应对应的回答")
    @GetMapping("/chat")
    public SseEmitter aiTask(@RequestParam("content") String content, @RequestParam("dialogId") Long dialogId) {
        System.out.println(content);
        List<Message> list = new ArrayList<>();
        Message message1 = Message.builder()
                .role(Role.system.getValue())
                .content("你是一位健康助手,目标任务: 你会为我解答各类健康知识,需求说明:你只能解答健康相关知识问题，其他问题回答: '我目前只解决健康相关问题'。")
                .build();
        list.add(message1);
        //根据当前用户和会话id获取历史对话记录
        List<MessageVO> list1 = aiService.showRecord(dialogId);
        list1.forEach(messageVO -> {
            Message message = Message.builder()
                    .content(messageVO.getContent())
                    .role(messageVO.getType() == 1 ? Role.user.getValue() : Role.assistant.getValue())
                    .build();
            list.add(message);
        });

        Message message = Message.builder()
                .content(content)
                .role(Role.user.getValue())
                .build();
        list.add(message);
        List<Tool> tools = new ArrayList<>();
        Tool tool = new Tool();
        tool.setType("web_search");
        WebSearch web = new WebSearch();
        web.setEnable(true);
        web.setSearch_mode("deep");
        tool.setWeb_search(web);
        tools.add(tool);
        RequestDTO request = RequestDTO.builder()
                .model(Module.Ultra_4.getValue())
                .messages(list)
                .max_tokens(4096)
                .top_k(4)
                .tools(tools)
                .temperature(0.5)
                .stream(Boolean.TRUE)
                .build();

        SseEmitter emitter;
        try {
            emitter = HttpClientUtil.doPostAIJson(aiProperties.getUrl(), request, aiProperties.getApiKey(), dialogId, aiService);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        aiService.addMessage(content, dialogId, (byte) 1);
        return emitter;

    }

    /**
     * 新增会话
     *
     * @return 新增会话id
     */
    @Operation(summary = "新增会话")
    @GetMapping("/addDialog")
    public Result addDialog() {

        return aiService.addDislog();
    }

    /**
     * 查询一个会话的所有消息记录
     *
     * @param dialogId 会话id
     * @return 一个会话的所有消息记录
     */
    @Operation(summary = "查询一个会话的所有消息记录")
    @GetMapping("/showRecord/{dialogId}")
    public Result showRecord(@PathVariable("dialogId") Long dialogId) {
        log.info("会话id:{}", dialogId);

        return Result.success(aiService.showRecord(dialogId));
    }

    /**
     * 展示会话主题列表
     *
     * @return 返回会话主题列表
     */
    @Operation(summary = "展示会话主题列表")
    @GetMapping("/showDialogs")
    public Result showDialogs() {

        return aiService.showDialogs();
    }

    /**
     * 根据id删除会话
     *
     * @param dialogId 会话id
     * @return 成功删除的信息
     */
    @Operation(summary = "根据id删除会话")
    @DeleteMapping("/deleteDialog/{dialogId}")
    public Result deleteDialog(@PathVariable Long dialogId) {

        return aiService.deleteDialogById(dialogId);
    }

    /**
     * 更新会话
     *
     * @param dialog 会话实体
     * @return 更新成功信息
     */
    @Operation(summary = "更新会话")
    @PutMapping("/updateDialog")
    public Result updateDialog(@RequestBody Dialog dialog) {

        log.info("Dialog:{}", dialog.toString());


        return aiService.updateDialog(dialog);
    }

}
