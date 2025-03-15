package com.kxdkcf.utils.base64;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;

public class Base64Utils {
    // 文件转Base64
    public static String fileToBase64(String filePath) throws IOException {
        byte[] bytes = Files.readAllBytes(Paths.get(filePath));
        return Base64.getEncoder().encodeToString(bytes);
    }

    // Base64转文件
    public static void base64ToFile(String base64, String outputPath) throws IOException {
        byte[] decodedBytes = Base64.getDecoder().decode(base64);
        Files.write(Paths.get(outputPath), decodedBytes);
    }
}