package com.admin.common.configuration.security;

import com.admin.common.exception.BaseBizCodeEnum;
import com.admin.common.util.ResponseUtil;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 未登录异常
 */
public class MyAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
        AuthenticationException authException) {

        // 尚未登录，请先登录
        ResponseUtil.out(response, BaseBizCodeEnum.NOT_LOGGED_IN_YET);
    }
}
