package com.kbs.security.starter.config;

import com.kbs.security.starter.filter.JwtAuthenticationFilter;
import com.kbs.security.starter.handler.AuthenticationEntryPointImpl;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 安全自动配置类
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@EnableConfigurationProperties(SecurityProperties.class)
public class SecurityAutoConfiguration {

    private final SecurityProperties securityProperties;

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityAutoConfiguration(SecurityProperties securityProperties,
                                     JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.securityProperties = securityProperties;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    /**
     * 密码编码器
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 认证管理器
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    /**
     * 安全过滤链配置
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        List<String> permitAllUrls = new ArrayList<>(Arrays.asList(securityProperties.getPermitAllUrls()));

        // SSE 流结束时，响应已经提交（committed），但 Spring Security 还想去处理异常导致报错
        permitAllUrls.add("/chat/chat");

        http.authorizeHttpRequests(authorize -> authorize
                // 虽然配置放行URL， 但是JwtAuthenticationFilter也要实现shouldNotFilter，因为请求先经过过滤器
                .requestMatchers(permitAllUrls.toArray(new String[0])).permitAll()
                .anyRequest().authenticated()
        );

        // 禁用 session
        http.sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        );

        // 禁用 CSRF
        http.csrf(AbstractHttpConfigurer::disable);

        // 自定义认证异常处理，返回统一Result格式
        http.exceptionHandling(exception -> exception
                .authenticationEntryPoint(new AuthenticationEntryPointImpl())
        );

        // 添加 JWT 过滤器
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
