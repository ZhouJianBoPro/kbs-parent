package com.kbs.security.starter.config;

import com.kbs.common.consts.ResultCodeConstant;
import com.kbs.security.starter.consts.SecurityConstant;
import com.kbs.security.starter.exception.CustomAuthenticationException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * JWT 工具类
 */
@Slf4j
@Component
public class JwtUtils {

    private final SecurityProperties securityProperties;

    private final SecretKey key;

    public JwtUtils(SecurityProperties securityProperties) {
        this.securityProperties = securityProperties;
        this.key = Keys.hmacShaKeyFor(securityProperties.getJwtSecret().getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 生成 Token
     */
    public String generateToken(String username, boolean isAccessToken) {

        Map<String, Object> claims = new HashMap<>();
        claims.put("type", isAccessToken ? "access" : "refresh");

        return Jwts.builder()
                .claims(claims)
                .subject(username)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + (isAccessToken ? getAccessTokenExpire() : getRefreshTokenExpire()) * 1000))
                .signWith(key)
                .compact();
    }

    /**
     * 获取username
     */
    public String getUserNameFromToken(String token, boolean isAccessToken) throws CustomAuthenticationException {

        // 1. 解析token
        Claims claims = parseToken(token);

        // 2. 验证 Token 类型
        if(!Objects.equals(isAccessToken ? SecurityConstant.TOKEN_TYPE_ACCESS : SecurityConstant.TOKEN_TYPE_REFRESH, claims.get("type"))) {
            throw new CustomAuthenticationException(ResultCodeConstant.UN_AUTHENTICATION, "无效的Token类型");
        }

        // 3. 获取用户名
        String username = claims.getSubject();
        if(!StringUtils.hasText(username)) {
            throw new CustomAuthenticationException(ResultCodeConstant.UN_AUTHENTICATION, "非法的Token");
        }

        // 4. 验证 Token 有效性
        if(claims.getExpiration().before(new Date())) {
            throw new CustomAuthenticationException(ResultCodeConstant.ACCESS_AUTH_EXPIRE, "Token已过期");
        }

        return username;
    }

    /**
     * 解析 Token
     */
    private Claims parseToken(String token) throws CustomAuthenticationException {

        try {
            return Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (Exception e) {
            log.error("Token解析失败", e);
            throw new CustomAuthenticationException(ResultCodeConstant.UN_AUTHENTICATION, "Token解析失败");
        }
    }

    /**
     * 获取 Access Token 过期时间
     * @return
     */
    public long getAccessTokenExpire() {
        return securityProperties.getAccessTokenExpire();
    }

    /**
     * 获取 Refresh Token 过期时间
     * @return
     */
    public long getRefreshTokenExpire() {
        return securityProperties.getRefreshTokenExpire();
    }
}
