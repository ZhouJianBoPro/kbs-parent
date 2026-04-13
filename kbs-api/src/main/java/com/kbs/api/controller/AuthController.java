package com.kbs.api.controller;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import com.kbs.api.dto.LoginRequest;
import com.kbs.api.service.AuthService;
import com.kbs.api.vo.LoginResponse;
import com.kbs.common.model.Result;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * 认证控制器
 */
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Resource
    private AuthService authService;

    /**
     * 登录
     */
    @PostMapping("/login")
    public Result<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        LoginResponse loginResponse = authService.login(loginRequest);
        return Result.success(loginResponse);
    }

    /**
     * 登出
     */
    @PostMapping("/logout")
    public Result<String> logout(Authentication authentication) {
        String username = authentication.getName();
        authService.logout(username);
        return Result.success("登出成功");
    }

    /**
     * 刷新Token
     */
    @GetMapping("/refreshToken")
    public Result<LoginResponse> refreshToken(@RequestParam("refreshToken") String refreshToken) {
        LoginResponse loginResponse = authService.refreshToken(refreshToken);
        return Result.success(loginResponse);
    }
}
