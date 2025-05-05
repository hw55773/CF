package com.kxdkcf.controller;

import com.kxdkcf.Result.Result;
import com.kxdkcf.context.ThreadLocalValueUser;
import com.kxdkcf.dto.UserDTO;
import com.kxdkcf.dto.UserUpdate;
import com.kxdkcf.service.IUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * BelongsProject:      demo-cf
 * BelongsPackage:      com.kxdkcf.controller
 * Author:              wenhao
 * CreateTime:          2024-12-27  13:51
 * Description:         TODO
 * Version:             1.0
 */
@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/user")
@Tag(name = "用户相关接口")
public class UserController {


    private final IUserService userService;


    /**
     * 根据用户id查询用户信息
     *
     * @param userId 用户id
     * @return 用户信息
     */
    @Operation(summary = "根据用户id获取用户的信息")
    @GetMapping("/{id}")
    public Result queryUser(@PathVariable("id") Long userId) {
        return userService.getById(userId);
    }

    /**
     * 用户注册
     *
     * @param userDTO 用户传输对象
     * @return 注册结果
     */
    @Operation(summary = "用户注册")
    @PostMapping("/register")
    public Result register(@RequestBody UserDTO userDTO) {

        return userService.insert(userDTO);
    }

    /**
     * 用户登录
     *
     * @param userDTO  用户传输对象
     * @param response 获取servlet response对象
     * @return 登录结果
     */
    @Operation(summary = "用户登录")
    @PostMapping("/login")
    public Result login(@RequestBody UserDTO userDTO, HttpServletResponse response) {

        return userService.login(userDTO, response);
    }

    @PutMapping("/update")
    public ResponseEntity<?> update(@RequestBody UserUpdate user) {

        return userService.update(user);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> delete() {

        Long userId = ThreadLocalValueUser.getThreadLocalValue(Long.class);

        return userService.deleteById(userId);
    }

    @PostMapping("/captcha")
    public ResponseEntity<?> createCaptcha(@RequestBody UserDTO user) {

        return userService.createCaptcha(user);
    }

}
