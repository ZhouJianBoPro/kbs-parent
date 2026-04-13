package com.kbs.api.service.impl;

import com.kbs.api.dto.LoginRequest;
import com.kbs.api.service.AppUserService;
import com.kbs.api.service.AuthService;
import com.kbs.api.vo.LoginResponse;
import com.kbs.common.consts.RedisConstant;
import com.kbs.common.consts.ResultCodeConstant;
import com.kbs.common.exception.BusinessException;
import com.kbs.core.entity.AppUser;
import com.kbs.security.starter.config.JwtUtils;
import com.kbs.security.starter.exception.CustomAuthenticationException;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.time.Duration;

/**
 * 认证服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    @Resource
    private AuthenticationManager authenticationManager;

    @Resource
    private JwtUtils jwtUtils;

    @Resource
    private RedissonClient redissonClient;

    @Resource
    private AppUserService appUserService;

    @Resource
    private UserDetailsService userDetailsService;

    @Override
    public LoginResponse login(LoginRequest loginRequest) {

        // 1. 认证
        Authentication authentication;
        try {
            UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword());
            authentication = authenticationManager.authenticate(usernamePasswordAuthenticationToken);
        } catch (Exception e) {
            log.warn("用户 {} 登录失败", loginRequest.getUsername(), e);
            throw new BusinessException("用户名或密码错误");
        }

        // 2. 获取认证返回的用户信息
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String username = userDetails.getUsername();

        // 3. 生成accessToken并保存到redis中
        String accessToken = jwtUtils.generateToken(username, true);
        String accessTokenKey = RedisConstant.TOKEN_PREFIX + username;
        redissonClient.getBucket(accessTokenKey).set(accessToken, Duration.ofSeconds(jwtUtils.getAccessTokenExpire()));

        // 4. 生成refreshToken并保存到redis中
        String refreshToken = jwtUtils.generateToken(username, false);
        String refreshTokenKey = RedisConstant.REFRESH_TOKEN_PREFIX + username;
        redissonClient.getBucket(refreshTokenKey).set(refreshToken, Duration.ofSeconds(jwtUtils.getRefreshTokenExpire()));

        log.info("用户 {} 登录成功", username);

        // 5. 返回响应
        AppUser appUser = appUserService.getByUsername(username);
        return buildLoginResponse(appUser, accessToken, refreshToken);
    }

    @Override
    public LoginResponse refreshToken(String refreshToken) {

        // 1. 解析并验证token，获取用户名
        String username = jwtUtils.getUserNameFromToken(refreshToken, false);

        // 2. 验证用户是否正常
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        if (userDetails == null) {
            throw new CustomAuthenticationException(ResultCodeConstant.UN_AUTHENTICATION, "用户不存在");
        }

        // 3. 校验 Redis 中是否存在（是否被登出/踢下线）
        String refreshTokenKey = RedisConstant.REFRESH_TOKEN_PREFIX + username;
        RBucket<String> storedToken = redissonClient.getBucket(refreshTokenKey);
        if (!storedToken.isExists() || !refreshToken.equals(storedToken.get())) {
            throw new CustomAuthenticationException(ResultCodeConstant.UN_AUTHENTICATION, "RefreshToken已失效");
        }

        // 4. 生成新的AccessToken并保存到redis
        String newAccessToken = jwtUtils.generateToken(username, true);
        String accessTokenKey = RedisConstant.TOKEN_PREFIX + username;
        redissonClient.getBucket(accessTokenKey).set(newAccessToken, Duration.ofSeconds(jwtUtils.getAccessTokenExpire()));

        // 5. 生成新的RefreshToken并更新到redis
        String newRefreshToken = jwtUtils.generateToken(username, false);
        redissonClient.getBucket(refreshTokenKey).set(newRefreshToken, Duration.ofSeconds(jwtUtils.getRefreshTokenExpire()));

        log.info("用户 {} Token 刷新成功", username);

        AppUser appUser = appUserService.getByUsername(username);
        return buildLoginResponse(appUser, newAccessToken, newRefreshToken);
    }

    @Override
    public void logout(String username) {

        // 删除 Redis 中的 Token
        String tokenKey = RedisConstant.TOKEN_PREFIX + username;
        redissonClient.getBucket(tokenKey).delete();

        // 删除 refreshToken
        String refreshTokenKey = RedisConstant.REFRESH_TOKEN_PREFIX + username;
        redissonClient.getBucket(refreshTokenKey).delete();

        log.info("用户 {} 登出", username);
    }

    /**
     * 构建登录响应
     */
    private LoginResponse buildLoginResponse(AppUser appUser, String accessToken, String refreshToken) {
        return LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .username(appUser.getUsername())
                .accountType(appUser.getAccountType())
                .build();
    }
}
