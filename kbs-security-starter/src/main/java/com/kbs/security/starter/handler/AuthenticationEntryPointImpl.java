package com.kbs.security.starter.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kbs.common.model.Result;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;

/**
 * @Desc: 认证失败处理器
 * @Author: zhoujianbo
 * @Version: 1.0
 * @Date: 2026/4/7 18:25
 **/
@Slf4j
public class AuthenticationEntryPointImpl implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {

        log.error("认证失败", authException);

        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/json;charset=UTF-8");
        Result<Object> result = Result.error(401, "身份认证失败");
        response.getWriter().write(new ObjectMapper().writeValueAsString(result));
    }
}
