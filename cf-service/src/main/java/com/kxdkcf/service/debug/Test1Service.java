package com.kxdkcf.service.debug;

import com.kxdkcf.enity.User;
import com.kxdkcf.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * BelongsProject:      demo-cf
 * BelongsPackage:      com.kxdkcf.service.Impl
 * Author:              wenhao
 * CreateTime:          2024-12-29  18:53
 * Description:         TODO
 * Version:             1.0
 */
@RequiredArgsConstructor
@Service
public class Test1Service {


    private final UserMapper userMapper;
    private final Test2Service test2Service;

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void test1() {

        User user = User.builder()
                .userName("kxdk")
                .password("123")
                .avatar("1")
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .build();

        userMapper.insertUser(user);
        //int i=1/0;

        try {
            test2Service.test2(user);
        } catch (Exception e) {
            System.out.println("test2()异常");
        }

        //throw new RuntimeException();

    }
}
