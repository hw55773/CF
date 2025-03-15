package com.kxdkcf.interceptor;

import cn.hutool.json.JSONUtil;
import com.kxdkcf.Result.Result;
import com.kxdkcf.constant.TokenKeyConstant;
import com.kxdkcf.context.ThreadLocalValueUser;
import com.kxdkcf.properties.JwtProperties;
import com.kxdkcf.utils.jwts.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.util.Map;

/**
 * BelongsProject:      demo-cf
 * BelongsPackage:      com.kxdkcf.interceptor
 * Author:              wenhao
 * CreateTime:          2025-01-01  19:27
 * Description:         TODO
 * Version:             1.0
 */
@RequiredArgsConstructor
@Component
@Slf4j
public class UserTokenInterceptor implements HandlerInterceptor {

    private final JwtProperties jwtProperties;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        String token = request.getHeader(TokenKeyConstant.TOKEN);
        //判断是否传了token
        if (token == null) {
            response.setStatus(401);
            String error = JSONUtil.toJsonPrettyStr(Result.error("NO_LOGIN"));
            response.getWriter().write(error);
            return false;
        }
        Map<String, Object> claims = JwtUtils.parseJwt(token, jwtProperties.getUserTokenKey());

        //判断是否解析成功
        if (claims != null) {
            Long user_id = Long.valueOf(claims.get(TokenKeyConstant.USER_ID).toString());
            ThreadLocalValueUser.setThreadLocalValue(user_id);
        } else {
            response.setStatus(401);
            String error = JSONUtil.toJsonStr(Result.error("登录过期,请重新登录!"));
            response.getWriter().write(error);
            return false;
        }

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        ThreadLocalValueUser.removeThreadLocalValue();
    }
}
