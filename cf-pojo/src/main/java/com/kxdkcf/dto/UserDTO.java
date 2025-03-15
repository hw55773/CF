package com.kxdkcf.dto;

import lombok.Builder;
import lombok.Data;

/**
 * BelongsProject:      demo-cf
 * BelongsPackage:      com.kxdkcf.dto
 * Author:              wenhao
 * CreateTime:          2024-12-27  13:07
 * Description:         用户传输对象
 * Version:             1.0
 */
@Data
@Builder
public class UserDTO {

    /**
     * 用户名。
     */
    private String userName;

    /**
     * 用户密码。
     */
    private String password;

    /**
     * 用户头像的链接地址。
     */
    private String avatar;
    /**
     * 用户邮箱
     */
    private String email;

    private String captcha;
}
