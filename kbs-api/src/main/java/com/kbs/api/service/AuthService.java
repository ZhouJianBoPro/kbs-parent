package com.kbs.api.service;

import com.kbs.api.dto.LoginRequest;
import com.kbs.api.vo.LoginResponse;

/**
 * 认证服务接口
 */
public interface AuthService {

    /**
     * 登录
     * @param loginRequest 登录请求
     * @return 登录响应
     */
    LoginResponse login(LoginRequest loginRequest);

    /**
     * 刷新令牌
     * @param refreshToken 刷新令牌
     * @return 登录响应
     */
    LoginResponse refreshToken(String refreshToken);

    /**
     * 登出
     * @param username 用户ID
     */
    void logout(String username);
}
