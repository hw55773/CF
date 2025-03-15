package com.kxdkcf.dto;

import lombok.Data;

/**
 * BelongsProject:      demo-cf
 * BelongsPackage:      com.kxdkcf.dto
 * Author:              wenhao
 * CreateTime:          2025-03-22  15:34
 * Description:         TODO
 * Version:             1.0
 */
@Data
public class UserUpdate {
    private String avatar;
    private String userName;
    private String email;
    private String currentPassword;
    private String newPassword;
}
