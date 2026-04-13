package com.kbs.api.vo;

import lombok.Builder;
import lombok.Data;

/**
 * 登录响应VO
 */
@Data
@Builder
public class LoginResponse {

    /**
     * 访问令牌
     */
    private String accessToken;

    /**
     * 刷新令牌
     */
    private String refreshToken;

    /**
     * 用户名
     */
    private String username;

    /**
     * 账户类型
     */
    private String accountType;
}
