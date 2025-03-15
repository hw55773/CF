package com.kxdkcf.utils.aliyun;

import com.aliyun.ocr_api20210707.models.RecognizeAllTextResponse;
import com.aliyun.tea.TeaException;

import java.io.IOException;
import java.io.InputStream;

import static com.aliyun.teautil.Common.assertAsString;

/**
 * BelongsProject:      demo-cf
 * BelongsPackage:      com.kxdkcf.utils.aliyun
 * Author:              wenhao
 * CreateTime:          2025-03-19  20:38
 * Description:         TODO
 * Version:             1.0
 */
public class OcrUtils {

    public static String recognizeImageText(OcrClient ocrClient, InputStream bodyStream) {
        RecognizeAllTextResponse response = null;
        String content = null;
        try {
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
            content = response.getBody().getData().getContent();

        }
        return content;
    }
}
