package com.kxdkcf.utils.jwts;

import io.jsonwebtoken.*;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;

/**
 * BelongsProject:      demo-cf
 * BelongsPackage:      com.kxdkcf.utils.jwts
 * Author:              wenhao
 * CreateTime:          2024-12-29  15:37
 * Description:         Jwt生成解析工具类
 * Version:             1.0
 */

public class JwtUtils {


    /**
     * 生成Jwt加密token
     *
     * @param securityKey        私有密钥
     * @param claims             加密body
     * @param ttl                过期时间
     * @param signatureAlgorithm 加密算法
     * @return token字符串
     */
    public static String createJwt(String securityKey, Map<String, Object> claims, long ttl, SignatureAlgorithm signatureAlgorithm) {

        return Jwts.builder()
                // 如果有私有声明，一定要先设置这个自己创建的私有的声明，这个是给builder的claim赋值，一旦写在标准的声明赋值之后，就是覆盖了那些标准的声明的
                .setClaims(claims)
                // 设置签名使用的签名算法和签名使用的秘钥
                .signWith(signatureAlgorithm, securityKey.getBytes(StandardCharsets.UTF_8))
                // 设置过期时间
                .setExpiration(new Date(System.currentTimeMillis() + ttl * 3600 * 1000))
                // 实际上，构建 JWT 并根据 JWT Compact Serialization  规则将其序列化为紧凑的 URL 安全字符串。
                .compact();
    }

    /**
     * 解析token
     *
     * @param token       需要解析的token
     * @param securityKey 私有密钥
     * @return 解析出来的实体
     */
    public static Claims parseJwt(String token, String securityKey) {

        try {
            // 得到DefaultJwtParser
            return Jwts.parser()
                    // 设置签名的秘钥
                    .setSigningKey(securityKey.getBytes(StandardCharsets.UTF_8))
                    // 设置需要解析的jwt
                    .parseClaimsJws(token).getBody();
        } catch (ExpiredJwtException e) {
            // 捕获到过期的JWT异常，表示JWT已经过了其有效期。
            e.printStackTrace();
            return null;
        } catch (UnsupportedJwtException e) {
            // 捕获到不支持的JWT异常，表示JWT的某些部分不被当前系统支持。
            e.printStackTrace();
            return null;
        } catch (MalformedJwtException e) {
            // 捕获到JWT格式异常，表示JWT的结构不正确或无法解析。
            e.printStackTrace();
            return null;
        } catch (SignatureException e) {
            // 捕获到签名异常，表示JWT的签名验证失败。
            e.printStackTrace();
            return null;
        } catch (IllegalArgumentException e) {
            // 捕获到非法参数异常，表示传递给JWT处理方法的参数不合法。
            e.printStackTrace();
            return null;
        }
    }
}
