package com.kxdkcf.utils.aliyun;

import com.aliyun.ocr_api20210707.Client;
import com.aliyun.ocr_api20210707.models.RecognizeAllTextRequest;
import com.aliyun.ocr_api20210707.models.RecognizeAllTextResponse;
import com.aliyun.teaopenapi.models.Config;
import com.aliyun.teautil.models.RuntimeOptions;
import com.kxdkcf.properties.AliOcrProperties;
import org.springframework.stereotype.Component;

import java.io.InputStream;

/**
 * BelongsProject:      demo-cf
 * BelongsPackage:      com.kxdkcf.utils.aliyun
 * Author:              wenhao
 * CreateTime:          2025-03-19  15:56
 * Description:         TODO
 * Version:             1.0
 */
@Component
public class OcrClient {
    private final Client client;

    public OcrClient(AliOcrProperties aliOcrProperties) throws Exception {
        Config config = new Config()
                .setAccessKeyId(aliOcrProperties.getId())
                .setAccessKeySecret(aliOcrProperties.getKey());
        config.endpoint = "ocr-api.cn-hangzhou.aliyuncs.com";
        this.client = new Client(config);
    }

    public RecognizeAllTextResponse stringUrl(String imageUrl, String type) throws Exception {
        RecognizeAllTextRequest request = new RecognizeAllTextRequest();
        request.setUrl(imageUrl);
        request.setType(type);
        return client.recognizeAllTextWithOptions(request, new RuntimeOptions());
    }

    public RecognizeAllTextResponse fileStream(InputStream fileStream, String type) throws Exception {

        RecognizeAllTextRequest request = new RecognizeAllTextRequest();
        request.setBody(fileStream);
        request.setType(type);
        return client.recognizeAllTextWithOptions(request, new RuntimeOptions());
    }
}
