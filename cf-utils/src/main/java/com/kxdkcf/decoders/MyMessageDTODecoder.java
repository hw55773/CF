package com.kxdkcf.decoders;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kxdkcf.dto.MessageDTO;
import jakarta.websocket.Decoder;
import jakarta.websocket.EndpointConfig;

public class MyMessageDTODecoder implements Decoder.Text<MessageDTO> {

    @Override
    public MessageDTO decode(String s) {
        // 将JSON字符串反序列化为MyMessageDTO对象
        // 这里可以使用Jackson、Gson或其他JSON库
        try {
            return new ObjectMapper().readValue(s, MessageDTO.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean willDecode(String s) {
        // 检查字符串是否可以被解码为MyMessageDTO对象
        // 例如，可以检查字符串是否符合预期的格式
        return s.startsWith("{") && s.endsWith("}");
    }

    @Override
    public void init(EndpointConfig config) {
        // 初始化解码器
    }

    @Override
    public void destroy() {
        // 销毁解码器
    }
}