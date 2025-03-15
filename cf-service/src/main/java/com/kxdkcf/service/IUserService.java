package com.kxdkcf.service;

import com.kxdkcf.Result.Result;
import com.kxdkcf.dto.UserDTO;
import com.kxdkcf.dto.UserUpdate;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;

/**
 * BelongsProject:      demo-cf
 * BelongsPackage:      com.kxdkcf.service
 * Author:              wenhao
 * CreateTime:          2024-12-27  13:49
 * Description:         TODO
 * Version:             1.0
 */
public interface IUserService {
    Result getById(Long userId);

    Result insert(UserDTO userDTO);

    Result login(UserDTO userDTO, HttpServletResponse response);

    ResponseEntity<?> update(UserUpdate user);

    ResponseEntity<?> deleteById(Long userId);

    ResponseEntity<?> createCaptcha(UserDTO user);
}
