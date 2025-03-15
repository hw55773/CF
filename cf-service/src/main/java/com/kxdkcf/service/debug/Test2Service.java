package com.kxdkcf.service.debug;

import com.kxdkcf.enity.User;
import com.kxdkcf.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * BelongsProject:      demo-cf
 * BelongsPackage:      com.kxdkcf.service.Impl
 * Author:              wenhao
 * CreateTime:          2024-12-29  19:21
 * Description:         TODO
 * Version:             1.0
 */
@RequiredArgsConstructor
@Service
public class Test2Service {

    private final UserMapper userMapper;

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    public void test2(User user) {
        user.setId(null);
        user.setUserName("wh");
        userMapper.insertUser(user);
        //throw new RuntimeException();

    }
}
