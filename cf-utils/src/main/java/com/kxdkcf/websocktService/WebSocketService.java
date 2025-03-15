package com.kxdkcf.websocktService;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.kxdkcf.Result.Result;
import com.kxdkcf.decoders.MyMessageDTODecoder;
import com.kxdkcf.dto.MessageDTO;
import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * BelongsProject:      demo-cf
 * BelongsPackage:      com.kxdkcf.websocktService
 * Author:              wenhao
 * CreateTime:          2024-12-30  16:04
 * Description:         TODO
 * Version:             1.0
 */
@Component
@ServerEndpoint(value = "/ws/aiChat/{uid}", decoders = MyMessageDTODecoder.class)
public class WebSocketService {

    /**
     * 存放会话对象
     */
    private static final Map<Long, Session> sessionMap = new ConcurrentHashMap<>();
    ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 链接上WebSocket服务触发的方法
     *
     * @param userId  连接者的id
     * @param session 连接的会话对象
     */
    @OnOpen
    public void onOpen(@PathParam("uid") Long userId, Session session) {

        Session session1 = sessionMap.putIfAbsent(userId, session);
        if (session1 != null) {
            System.out.println("已经存在会话" + userId + "=>" + session1);
        } else {
            System.out.println(userId + "=>" + session + "成功加入连接");
        }


    }

    /**
     * 客户端发送消息触发
     *
     * @param msg    客户端发送json格式数据解析成MessageDTO对象
     * @param userId 用户id
     */
    @OnMessage
    public void onMessage(MessageDTO msg, @PathParam("{uid}") Long userId, Session session) {

        String msg1 = msg.getMsg();
        if (msg1 == null || msg1.trim().equals("")) {
            return;
        }
        System.out.println("用户:" + userId + "\n" + "发送消息:" + msg1 + "\n" + "Session:" + session.toString());
    }

    /**
     * 用户断开连接时的处理
     */
    @OnClose
    public void onClose(@PathParam("uid") Long userId, Session session) {
        Session remove = sessionMap.remove(userId);
        System.out.println();
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        throwable.printStackTrace();
    }

    /**
     * 向指定客户端推消息
     *
     * @param userId 用户id
     * @param result 统一结果集
     */
    public void sendToUser(Long userId, Result result) {
        Session session = sessionMap.get(userId);//获取对应用户的session对象
        objectMapper.registerModule(new JavaTimeModule()); // 注册 Java 8 时间模块
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS); // 禁用时间戳
        if (session != null && session.isOpen()) {

            //String json = JSONUtil.toJsonPrettyStr(result);//转成换行可读的
            //String jsonStr = JSONUtil.toJsonStr(result);//转成紧凑一行的
            String jsonStr = "";
            try {
                jsonStr = objectMapper.writeValueAsString(result);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
            session.getAsyncRemote().sendText(jsonStr);//异步非阻塞发送
            //session.getBasicRemote();//同步阻塞发送

        }
    }

    /**
     * 发送给所有客户端
     *
     * @param result 统一结果集
     */
    public void sendToAll(Result result) {

        objectMapper.registerModule(new JavaTimeModule()); // 注册 Java 8 时间模块
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS); // 禁用时间戳
        Set<Map.Entry<Long, Session>> entries = sessionMap.entrySet();
        String json = "";
        try {
            json = objectMapper.writeValueAsString(result);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        //逐一发送
        for (Map.Entry<Long, Session> entry : entries) {
            entry.getValue().getAsyncRemote().sendText(json);
        }
    }
}
