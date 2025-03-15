package com.kxdkcf;

import cn.hutool.http.Header;
import cn.hutool.json.JSONUtil;
import com.aliyun.ocr_api20210707.models.RecognizeAllTextResponse;
import com.aliyun.tea.TeaException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.kxdkcf.Result.Result;
import com.kxdkcf.ai.requset.Message;
import com.kxdkcf.ai.requset.RequestDTO;
import com.kxdkcf.ai.response.RespondsDTO;
import com.kxdkcf.constant.SignatureAlgorithmConstant;
import com.kxdkcf.constant.TokenKeyConstant;
import com.kxdkcf.constant.ai.Module;
import com.kxdkcf.constant.ai.Role;
import com.kxdkcf.context.ThreadLocalValueUser;
import com.kxdkcf.dto.HealthDataDTO;
import com.kxdkcf.dto.PageTopicDTO;
import com.kxdkcf.enity.Topic;
import com.kxdkcf.enity.User;
import com.kxdkcf.enity.UserTest;
import com.kxdkcf.httputils.HttpClientUtil;
import com.kxdkcf.mapper.TopicMapper;
import com.kxdkcf.mapper.UserMapper;
import com.kxdkcf.properties.*;
import com.kxdkcf.service.IHealthService;
import com.kxdkcf.service.IRecommendService;
import com.kxdkcf.service.Impl.TopicServiceImpl;
import com.kxdkcf.service.debug.Test1Service;
import com.kxdkcf.utils.aliyun.OcrClient;
import com.kxdkcf.utils.jwts.JwtUtils;
import com.kxdkcf.vo.PageTopicBean;
import com.kxdkcf.vo.TopicDetailVO;
import com.kxdkcf.vo.TopicVO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.aliyun.teautil.Common.assertAsString;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CfServiceApplicationTests {

    @Autowired
    TopicMapper topicMapper;
    @Autowired
    TopicServiceImpl topicService;
    @Autowired
    AiProperties aiProperties;
    @Autowired
    Test1Service test1Service;
    @Autowired
    IRecommendService userRecommendServiceImpl;
    @Autowired
    PathHandler pathHandler;
    @Autowired
    JwtProperties jwtProperties;
    @Autowired
    UserMapper userMapper;
    @Autowired
    OcrClient ocrClient;
    @Autowired
    AliOcrProperties aliOcrProperties;
    @Autowired
    IHealthService healthService;
    @Autowired
    HotPostEvaluatorProperties hotPostEvaluatorProperties;

    @Autowired
    JavaMailSender javaMailSender;

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    void hutool() {
        System.out.println(Header.AUTHORIZATION.getValue());
    }

    @Test
    void contextLoads() {
        PageTopicDTO pageTopicDTO = new PageTopicDTO();
        pageTopicDTO.setUserId(2L);
        pageTopicDTO.setPage(1);
        pageTopicDTO.setPageSize(5);
        Result result = userRecommendServiceImpl.queryTopic(pageTopicDTO);
        PageTopicBean data = (PageTopicBean) result.getData();
        data.getTopicList().forEach(topic -> {
            System.out.println(topic.toString());
        });
        System.out.println(data.getTotal());
    }

    @Test
    void path() {
        pathHandler.getAddPathList().forEach(System.out::println);
        pathHandler.getExcludePathList().forEach(System.out::println);

    }

    @Test
    void threadLocalValue() {

        ThreadLocalValueUser.setThreadLocalValue(1L);
        Long threadLocalValue = ThreadLocalValueUser.getThreadLocalValue(Long.class);
        System.out.println(threadLocalValue);
    }

    @Test
    void jwt() {
        String tokenName = jwtProperties.getTokenName();
        String userTokenKey = jwtProperties.getUserTokenKey();
        long ttl = jwtProperties.getTtl();
        System.out.println("tokenName = " + tokenName);
        System.out.println("ttl = " + ttl);
        System.out.println("userTokenKey = " + userTokenKey);
    }

    @Test
    void createJwt() {
        Map<String, Object> map = new ConcurrentHashMap<>();
        map.put(TokenKeyConstant.USER_ID, 4L);
        String token = JwtUtils.createJwt(jwtProperties.getUserTokenKey(), map, 2L, SignatureAlgorithmConstant.HS256);
        System.out.println("token = " + token);
    }

    @Test
    void parseJwt() {

        String token = "eyJhbGciOiJIUzI1NiJ9.eyJ1c2VyX2lkIjo0LCJleHAiOjE3MzU0Njk2NTF9.tf-hQVBLrtSkMfJd0dLeDeH7aEngFho3iCqFOICgmWI";
        Map<String, Object> claims = JwtUtils.parseJwt(token, jwtProperties.getUserTokenKey());
        Object o = null;
        if (claims != null) {
            o = claims.get(TokenKeyConstant.USER_ID);
        }
        System.out.println("o = " + o);
    }

    @Test
    void transcational() {

        test1Service.test1();
    }

    @Test
    void toJsonStr() {
        UserTest test = new UserTest();
        test.setAge(22L);
        test.setName("kxdk");
        test.setTime(LocalDateTime.now());
        Topic topic = new Topic();
        topic.setId(1L);
        topic.setContent("wwwwwwww");
        topic.setFavoritesNumber(3L);
        topic.setTitle("hhhhhhh");
        topic.setCreateTime(LocalDateTime.now());
        topic.setUpdateTime(LocalDateTime.now());
        test.setTopic(topic);
        System.out.println(JSONUtil.toJsonStr(test));
        System.out.println("=======================");
        System.out.println(JSONUtil.toJsonPrettyStr(test));
        System.out.println("=======================");
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule()); // 注册 Java 8 时间模块
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS); // 禁用时间戳

        try {
            System.out.println(objectMapper.writeValueAsString(test));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }


    }

    @Test
    void properties() {

        System.out.println(aiProperties.toString());
    }

    @Test
    void aiChat() {

        String url = aiProperties.getUrl();

        List<Message> list = new ArrayList<>();
        Message message = Message.builder()
                .content("为什么查询不了天气？")
                .role(Role.user.getValue())
                .build();
        list.add(message);
        RequestDTO request = RequestDTO.builder()
                .model(Module.lite.getValue())
                .messages(list)
                .build();

        String key = aiProperties.getApiKey();
        RespondsDTO respondsDTO = null;
        try {
            respondsDTO = HttpClientUtil.doPostAIJson(url, request, key, RespondsDTO.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        String content = respondsDTO.getChoices().get(0).getMessage().getContent();
        content.lines().forEach(System.out::println);

    }

    @Test
    void test() {
        User user = userMapper.getUserById(20L);
        System.out.println(user);
    }

    @Test
    void testTopicMapper() {
        TopicDetailVO topicDetailVO = topicMapper.selectTopicDetails(4L);
        System.out.println(topicDetailVO.toString());
    }

    @Test
    void testTopicService() {
        ThreadLocalValueUser.setThreadLocalValue(23L);
//        Result result = topicService.queryTopicDetails(1L);
//        System.out.println(result.toString());
        PageTopicDTO pageTopicDTO = new PageTopicDTO();
        pageTopicDTO.setPage(1);
        pageTopicDTO.setPageSize(10);
        List<TopicVO> topicVOList = topicService.queryCollected(pageTopicDTO).getTopicList();
        System.out.println(topicVOList);
    }

    @Test
    void healthTest() {
        HealthDataDTO healthDataDTO = new HealthDataDTO();
        healthDataDTO.setAge(22);
        healthDataDTO.setBloodPressure("120/80");
        healthDataDTO.setGender(0);
        healthDataDTO.setHeight(171.0);
        healthDataDTO.setWeight(80.0);
        healthDataDTO.setVision("5.0");
        String string = healthDataDTO.toString();
        System.out.println(string);
    }

    @Test
    void ocrTest() throws FileNotFoundException {

        RecognizeAllTextResponse response = null;

        try (InputStream bodyStream = new FileInputStream("C:/Users/文化/Downloads/IMG_20250319_013931.jpg")) {
            response = ocrClient.fileStream(bodyStream, "Advanced");
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (TeaException error) {
            // 此处仅做打印展示，请谨慎对待异常处理，在工程项目中切勿直接忽略异常。
            // 错误 message
            System.out.println(error.getMessage());
            // 诊断地址
            System.out.println(error.getData().get("Recommend"));
            assertAsString(error.message);
        } catch (Exception error) {
            System.err.println(error.getMessage());
        }

        if (response != null) {
            String content = response.getBody().getData().getContent();
            System.out.println(content);
        }

    }

    @Test
    void analyse() {
        // healthService.getHealthAnalyse(26L);
        healthService.getHealthRecommendations(26L);
    }

    @Test
    void spark() throws JsonProcessingException {

        List<Long> list = new ArrayList<>();
        list.add(1L);
        list.add(2L);
        list.add(3L);

        stringRedisTemplate.opsForValue().set("wenhao", objectMapper.writeValueAsString(list));
        String wenhao = stringRedisTemplate.opsForValue().get("wenhao");
        List<Long> list1 = objectMapper.readValue(wenhao, new TypeReference<>() {
        });
        System.out.println(list1.toString());

    }

    @Test
    void mail() {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setSubject("授权登录");
        message.setText("欢迎使用个人健康管理系统");
        message.setTo("hwen55773@gmail.com");
        message.setFrom("2073578174@qq.com");
        javaMailSender.send(message);
    }

    @Test
    void topic() {
//        List<Topic> topicList = topicMapper.selectCount();
//        topicList.forEach(System.out::println);
        System.out.println(hotPostEvaluatorProperties.toString());
    }
}
