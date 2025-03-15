package com.kxdkcf.handler;

import com.kxdkcf.Result.Result;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * BelongsProject:      demo-cf
 * BelongsPackage:      com.kxdkcf.handler
 * Author:              wenhao
 * CreateTime:          2024-12-29  21:38
 * Description:         全局异常处理器
 * Version:             1.0
 */
@RestControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler(Exception.class)
    public ResponseEntity<Result> exceptionAllHandlers(Exception e, HttpServletRequest request) {
        e.printStackTrace();

        return ResponseEntity.status(500).body(Result.error("服务器异常!"));
    }

}
