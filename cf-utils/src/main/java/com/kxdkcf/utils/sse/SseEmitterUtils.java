package com.kxdkcf.utils.sse;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.concurrent.ConcurrentHashMap;

public class SseEmitterUtils {

    // 用于存储所有活跃的 SseEmitter 连接
    private static final ConcurrentHashMap<Long, SseEmitter> emitterMap = new ConcurrentHashMap<>();

    /**
     * 创建并返回一个新的 SseEmitter 对象
     *
     * @param clientId 客户端标识，作为唯一ID
     * @return 创建的 SseEmitter 对象
     */
    public static SseEmitter createSseEmitter(Long clientId) {
        SseEmitter emitter = new SseEmitter();

        // 设置超时，避免连接长时间无响应
        //emitter.setTimeout(TimeUnit.MINUTES.toMillis(1)); // 设置超时为1分钟，可以根据需求调整

        // 处理连接超时
        emitter.onTimeout(() -> {
            System.out.println("Client " + clientId + " connection timed out.");
            removeEmitter(clientId);
        });

        // 处理连接正常完成
        emitter.onCompletion(() -> {
            System.out.println("Client " + clientId + " connection completed.");
            removeEmitter(clientId);
        });

        // 将该 SseEmitter 存入集合中，方便后续管理
        emitterMap.put(clientId, emitter);
        return emitter;
    }

    /**
     * 向指定的 SseEmitter 推送消息
     *
     * @param clientId 客户端标识
     * @param message  消息内容
     */
    public static void sendMessage(Long clientId, String message) {
        SseEmitter emitter = emitterMap.get(clientId);
        if (emitter != null) {
            try {
                emitter.send(message); // 发送消息
            } catch (Exception e) {
                System.err.println("Failed to send message to client " + clientId + ": " + e.getMessage());
                emitter.completeWithError(e); // 如果发送失败，标记为错误
                removeEmitter(clientId);
            }
        } else {
            System.err.println("Emitter for client " + clientId + " not found.");
        }
    }

    /**
     * 完成并移除指定客户端的 SseEmitter
     *
     * @param clientId 客户端标识
     */
    public static void completeEmitter(Long clientId) {
        SseEmitter emitter = emitterMap.remove(clientId);
        if (emitter != null) {
            emitter.complete(); // 完成当前连接
        } else {
            System.err.println("Emitter for client " + clientId + " not found.");
        }
    }

    /**
     * 移除指定客户端的 SseEmitter
     *
     * @param clientId 客户端标识
     */
    public static void removeEmitter(Long clientId) {
        emitterMap.remove(clientId); // 从集合中移除 SseEmitter
    }

    /**
     * 广播消息给所有客户端
     *
     * @param message 消息内容
     */
    public static void broadcastMessage(String message) {
        for (SseEmitter emitter : emitterMap.values()) {
            try {
                emitter.send(message);
            } catch (Exception e) {
                emitter.completeWithError(e); // 发送失败，关闭该连接
            }
        }
    }
}
