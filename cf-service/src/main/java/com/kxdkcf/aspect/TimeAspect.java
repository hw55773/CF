package com.kxdkcf.aspect;

import com.kxdkcf.annotation.AutoTime;
import com.kxdkcf.constant.OperationType;
import com.kxdkcf.constant.TimeConstant;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.time.LocalDateTime;

/**
 * BelongsProject:      demo-cf
 * BelongsPackage:      com.kxdkcf.aspect
 * Author:              wenhao
 * CreateTime:          2025-01-07  14:55
 * Description:         TODO
 * Version:             1.0
 */
@Slf4j
@Component
@Aspect
public class TimeAspect {

    @Pointcut("@annotation(com.kxdkcf.annotation.AutoTime)")
    public void ptTime() {
    }


    @Before("ptTime()")
    public void before(JoinPoint joinPoint) {

        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        AutoTime annotation = methodSignature.getMethod().getAnnotation(AutoTime.class);
        Object[] args = joinPoint.getArgs();
        //获取实体对象
        Object enmity = args[0];
        OperationType value = annotation.value();
        if (value == OperationType.INSERT) {


            try {
                //获取实体对象的操作时间的方法
                Method setCreateTime = enmity.getClass().getDeclaredMethod(TimeConstant.SET_CREATE_TIME, LocalDateTime.class);
                Method setUpdateTime = enmity.getClass().getDeclaredMethod(TimeConstant.SET_UPDATE_TIME, LocalDateTime.class);

                //执行实体方法并赋值
                setCreateTime.invoke(enmity, LocalDateTime.now());
                setUpdateTime.invoke(enmity, LocalDateTime.now());


            } catch (Exception e) {
                throw new RuntimeException(e);
            }

        } else if (value == OperationType.UPDATE) {
            try {
                //获取实体对象的操作时间的方法
                Method setUpdateTime = enmity.getClass().getDeclaredMethod(TimeConstant.SET_UPDATE_TIME, LocalDateTime.class);

                //执行实体方法并赋值
                setUpdateTime.invoke(enmity, LocalDateTime.now());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        log.info("before!");

    }

    @Around("ptTime()")
    public Object around(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {

        log.info("around before!");
        Object proceed = proceedingJoinPoint.proceed();
        log.info("around after!");
        return proceed;
    }


}
