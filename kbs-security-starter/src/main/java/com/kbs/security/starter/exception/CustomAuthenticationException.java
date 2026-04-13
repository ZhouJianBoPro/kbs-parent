package com.kbs.security.starter.exception;

import lombok.Getter;
import org.springframework.security.core.AuthenticationException;

/**
 * @Desc: 认证异常
 * @Author: zhoujianbo
 * @Version: 1.0
 * @Date: 2026/3/23 11:07
 **/
@Getter
public class CustomAuthenticationException extends AuthenticationException {

    private static final long serialVersionUID = 1L;

    private final Integer code;

    public CustomAuthenticationException(Integer code, String message) {
        super(message);
        this.code = code;
    }
}
