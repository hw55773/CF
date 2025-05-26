package com.kxdkcf.httputils;

import cn.hutool.http.Header;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kxdkcf.ai.requset.RequestDTO;
import com.kxdkcf.service.IAiService;
import com.kxdkcf.utils.pool.ThreadPool;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;

/**
 * Http工具类
 */
public class HttpClientUtil {

    static final int TIMEOUT_MSEC = 5 * 100000;
    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 发送GET方式请求
     *
     * @param url      请求路径
     * @param paramMap 请求体
     * @return
     */
    public static String doGet(String url, Map<String, String> paramMap) {
        // 创建Httpclient对象
        CloseableHttpClient httpClient = HttpClients.createDefault();

        String result = "";
        CloseableHttpResponse response = null;

        try {
            URIBuilder builder = new URIBuilder(url);
            if (paramMap != null) {
                for (String key : paramMap.keySet()) {
                    builder.addParameter(key, paramMap.get(key));
                }
            }
            URI uri = builder.build();

            //创建GET请求
            HttpGet httpGet = new HttpGet(uri);

            //发送请求
            response = httpClient.execute(httpGet);

            //判断响应状态
            if (response.getStatusLine().getStatusCode() == 200) {
                result = EntityUtils.toString(response.getEntity(), "UTF-8");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                response.close();
                httpClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return result;
    }

    /**
     * 发送POST方式请求
     *
     * @param url      请求路径
     * @param paramMap 请求体
     * @return
     * @throws IOException
     */
    public static String doPost(String url, Map<String, String> paramMap) throws IOException {
        // 创建Httpclient对象
        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse response = null;
        String resultString = "";

        try {
            // 创建Http Post请求
            HttpPost httpPost = new HttpPost(url);

            // 创建参数列表
            if (paramMap != null) {
                List<NameValuePair> paramList = new ArrayList<>();
                for (Map.Entry<String, String> param : paramMap.entrySet()) {
                    paramList.add(new BasicNameValuePair(param.getKey(), param.getValue()));
                }
                // 模拟表单
                UrlEncodedFormEntity entity = new UrlEncodedFormEntity(paramList);
                httpPost.setEntity(entity);
            }

            httpPost.setConfig(builderRequestConfig());

            // 执行http请求
            response = httpClient.execute(httpPost);

            resultString = EntityUtils.toString(response.getEntity(), "UTF-8");
        } catch (Exception e) {
            throw e;
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return resultString;
    }

    /**
     * 发送POST方式请求
     *
     * @param url      请求路径
     * @param paramMap 请求体
     * @return 结果信息
     */
    public static String doPost4Json(String url, Map<String, String> paramMap) throws IOException {
        // 创建Httpclient对象
        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse response = null;
        String resultString = "";

        try {
            // 创建Http Post请求
            HttpPost httpPost = new HttpPost(url);

            if (paramMap != null) {
                //构造json格式数据
                JSONObject jsonObject = new JSONObject();
                jsonObject.putAll(paramMap);
                StringEntity entity = new StringEntity(jsonObject.toString(), "utf-8");
                //设置请求编码
                entity.setContentEncoding("utf-8");
                //设置数据类型
                entity.setContentType("application/json");
                httpPost.setEntity(entity);
            }

            httpPost.setConfig(builderRequestConfig());

            // 执行http请求
            response = httpClient.execute(httpPost);

            resultString = EntityUtils.toString(response.getEntity(), "UTF-8");
        } catch (Exception e) {
            throw e;
        } finally {
            try {
                response.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return resultString;
    }


    /**
     * 发送POST方式请求
     *
     * @param url     请求路径
     * @param request 请求体
     * @param key     身份令牌
     * @param type    结果封装
     * @return 非流式结果信息
     */
    public static <E> E doPostAIJson(String url, RequestDTO request, String key, Class<E> type) throws IOException {
        // 创建Httpclient对象
        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse response = null;
        String resultString = "";

        try {
            // 创建Http Post请求
            HttpPost httpPost = new HttpPost(url);

            if (request != null) {
                //构造json格式数据
                String jsonStr = JSONUtil.toJsonStr(request);
                StringEntity entity = new StringEntity(jsonStr, "utf-8");
                //设置请求编码
                entity.setContentEncoding("utf-8");
                //设置数据类型
                entity.setContentType("application/json");
                httpPost.setHeader(Header.CONTENT_TYPE.getValue(), "application/json");
                httpPost.setHeader(Header.AUTHORIZATION.getValue(), key);
                httpPost.setEntity(entity);
            }

            httpPost.setConfig(builderRequestConfig());

            // 执行http请求
            response = httpClient.execute(httpPost);

            resultString = EntityUtils.toString(response.getEntity(), "UTF-8");
        } catch (Exception e) {
            throw e;
        } finally {
            httpClient.close();
            try {
                if (response != null) {
                    response.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return JSONUtil.toBean(resultString, type);
    }

    /**
     * 发送POST方式请求
     *
     * @param url       请求路径
     * @param request   请求体
     * @param dialogId
     * @param aiService
     * @return 流式结果信息
     */
    public static SseEmitter doPostAIJson(String url, RequestDTO request, String key, Long dialogId, IAiService aiService) throws IOException {
        // 创建 SseEmitter
        SseEmitter emitter = new SseEmitter();
        // 创建线程安全的字符串缓存
        StringBuilder stringBuilder = new StringBuilder();
        // 创建Httpclient对象
        // 启用异步处理(线程池)
        ExecutorService threadPool = ThreadPool.getThreadPool();
        threadPool.execute(() -> {
            try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
                // 创建 GET 请求
                HttpPost httpPost = new HttpPost(url); //  AI 接口地址
                if (request != null && request.getStream().equals(Boolean.TRUE)) {
                    //构造json格式数据
                    String jsonStr = JSONUtil.toJsonStr(request);
                    StringEntity entity = new StringEntity(jsonStr, "utf-8");
                    //设置请求编码
                    entity.setContentEncoding("utf-8");
                    //设置数据类型
                    entity.setContentType("application/json");
                    httpPost.setHeader(Header.CONTENT_TYPE.getValue(), "application/json");
                    httpPost.setHeader(Header.AUTHORIZATION.getValue(), key);
                    httpPost.setEntity(entity);
                }
                httpPost.setConfig(builderRequestConfig());
                // 执行请求
                try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
                    BufferedReader reader = new BufferedReader(
                            new InputStreamReader(response.getEntity().getContent())
                    );

                    String line;
                    while ((line = reader.readLine()) != null) {
                        if (!line.isEmpty()) {
                            String jsonData = line.substring(5).trim();
                            // 检查流结束标志
                            if ("[DONE]".equals(jsonData)) {
                                emitter.send(SseEmitter.event().data("[DONE]"));
                                break; // 流式数据结束
                            }
                            // 使用 Hutool 解析 JSON 数据
                            JSONObject jsonObject = JSONUtil.parseObj(jsonData);


                            // 提取 delta 节点的 content
                            JSONArray choices = jsonObject.getJSONArray("choices");
                            JSONObject delta = choices.getJSONObject(0).getJSONObject("delta");
                            if (delta.containsKey("content")) {
                                String content = delta.getStr("content");

                                // 发送数据到前端
                                emitter.send(SseEmitter.event().data(content));
                                stringBuilder.append(content);
                            }
                        }
                    }

                    // 释放资源
                    EntityUtils.consume(response.getEntity());
                }
                if (stringBuilder.length() > 0) {
                    aiService.addMessage(stringBuilder.toString(), dialogId, (byte) 0);
                }
                // 传输完成
                emitter.complete();
            } catch (Exception e) {
                // 发生异常时完成传输
                emitter.completeWithError(e);
            }
        });

        return emitter;
    }

    private static RequestConfig builderRequestConfig() {
        return RequestConfig.custom()
                .setConnectTimeout(TIMEOUT_MSEC)
                .setConnectionRequestTimeout(TIMEOUT_MSEC)
                .setSocketTimeout(TIMEOUT_MSEC).build();
    }


}
