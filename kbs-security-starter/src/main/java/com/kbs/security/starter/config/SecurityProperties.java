package com.kbs.security.starter.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 安全配置属性
 */
@Data
@ConfigurationProperties(prefix = "kbs.security")
public class SecurityProperties {

    /**
     * JWT 密钥
     */
    private String jwtSecret;

    /**
     * Access Token 过期时间（秒）
     */
    private Long accessTokenExpire;

    /**
     * Refresh Token 过期时间（秒）
     */
    private Long refreshTokenExpire;

    /**
     * 放行URL
     */
    private String[] permitAllUrls;
}
