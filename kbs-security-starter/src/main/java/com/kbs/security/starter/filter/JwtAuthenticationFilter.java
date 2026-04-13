package com.kbs.security.starter.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kbs.common.consts.RedisConstant;
import com.kbs.common.consts.ResultCodeConstant;
import com.kbs.common.model.Result;
import com.kbs.security.starter.config.JwtUtils;
import com.kbs.security.starter.config.SecurityProperties;
import com.kbs.security.starter.consts.SecurityConstant;
import com.kbs.security.starter.exception.CustomAuthenticationException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JWT 认证过滤器
 */
@Slf4j
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;
    private final RedissonClient redissonClient;
    private final UserDetailsService userDetailsService;
    private final SecurityProperties securityProperties;

    public JwtAuthenticationFilter(JwtUtils jwtUtils,
                                   RedissonClient redissonClient,
                                   UserDetailsService userDetailsService,
                                   SecurityProperties securityProperties) {
        this.jwtUtils = jwtUtils;
        this.redissonClient = redissonClient;
        this.userDetailsService = userDetailsService;
        this.securityProperties = securityProperties;
    }

    /**
     * 接口放行必须要实现这个接口，请求进来先经过该过滤器，然后才走到 Spring Security 权限判断
     * @param request
     * @return
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        String[] permitAllUrls = securityProperties.getPermitAllUrls();
        for (String url : permitAllUrls) {
            if (path.contains(url)) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        UserDetails userDetails;

        try {
            String token = getTokenFromRequest(request);

            // 1. 解析并验证token，获取用户名
            String username = jwtUtils.getUserNameFromToken(token, true);

            // 2. 获取用户信息，验证用户状态
            userDetails = userDetailsService.loadUserByUsername(username);
            if(userDetails == null) {
                throw new CustomAuthenticationException(ResultCodeConstant.UN_AUTHENTICATION, "用户不存在");
            }

            // 3. 验证 Redis 中是否存在（是否被登出或失效）
            RBucket<String> bucket = redissonClient.getBucket(RedisConstant.TOKEN_PREFIX + username);
            if(!bucket.isExists()) {
                throw new CustomAuthenticationException(ResultCodeConstant.ACCESS_AUTH_EXPIRE, "Token已失效");
            }

            // 4. 验证 Token 是否一致（是否踢下线）
            if(!bucket.get().equals(token)) {
                throw new CustomAuthenticationException(ResultCodeConstant.UN_AUTHENTICATION, "该账号已在其他地方登录");
            }

        } catch (CustomAuthenticationException e) {
            writeResponse(response, e);
            return;
        }

        // 5. 设置认证信息
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 6. 继续处理请求
        filterChain.doFilter(request, response);
    }

    private void writeResponse(HttpServletResponse response, CustomAuthenticationException exception) throws IOException {

        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/json;charset=UTF-8");
        Result<Object> result = Result.error(exception.getCode(), exception.getMessage());
        response.getWriter().write(new ObjectMapper().writeValueAsString(result));
    }

    /**
     * 从请求中获取 Token
     */
    private String getTokenFromRequest(HttpServletRequest request) {

        String bearerToken = request.getHeader(SecurityConstant.HEADER_NAME);
        if(!StringUtils.hasText(bearerToken)) {
            throw new CustomAuthenticationException(ResultCodeConstant.UN_AUTHENTICATION, "Token不存在");
        }

        String tokenPrefix = SecurityConstant.TOKEN_PREFIX;
        if(!bearerToken.startsWith(tokenPrefix)) {
            throw new CustomAuthenticationException(ResultCodeConstant.UN_AUTHENTICATION, "无效的Token");
        }

        return bearerToken.substring(tokenPrefix.length());
    }
}
