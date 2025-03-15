package com.kxdkcf.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * BelongsProject:      demo-cf
 * BelongsPackage:      com.kxdkcf.vo
 * Author:              wenhao
 * CreateTime:          2024-12-27  13:09
 * Description:         用户视图对象
 * Version:             1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserVO {

    /**
     * 用户的唯一标识符。
     */
    private Long id;

    /**
     * 用户名。
     */
    private String userName;

    /**
     * 用户密码。
     */
    //private String password;

    /**
     * 用户头像的链接地址。
     */
    private String avatar;
    /**
     * 用户邮箱
     */
    private String email;

    /**
     * 会话主题id
     */
    private Long dialogId;

    /**
     * 用户创建时间。
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    /**
     * 用户信息最后更新时间。
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}
