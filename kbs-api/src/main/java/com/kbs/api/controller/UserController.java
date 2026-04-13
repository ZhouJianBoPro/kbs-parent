package com.kbs.api.controller;

import com.kbs.api.dto.RegisterRequest;
import com.kbs.api.dto.UpdatePasswordRequest;
import com.kbs.api.dto.UpdateUserInfoRequest;
import com.kbs.api.service.UserService;
import com.kbs.api.vo.UserInfoVO;
import com.kbs.aspect.annotation.LimitRequest;
import com.kbs.common.model.Result;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * 用户控制器
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserService userService;

    /**
     * 注册
     */
    @PostMapping("/register")
    public Result<String> register(@Valid @RequestBody RegisterRequest registerRequest) {
        String userId = userService.register(registerRequest);
        return Result.success(userId);
    }

    /**
     * 获取当前用户信息
     */
    @GetMapping("/info")
    public Result<UserInfoVO> getUserInfo(Authentication authentication) {
        String username = authentication.getName();
        UserInfoVO userInfo = userService.getUserInfo(username);
        return Result.success(userInfo);
    }

    /**
     * 修改密码
     */
    @LimitRequest
    @PutMapping("/password")
    public Result<String> updatePassword(@Valid @RequestBody UpdatePasswordRequest updatePasswordRequest,
                                          Authentication authentication) {
        String username = authentication.getName();
        userService.updatePassword(username, updatePasswordRequest);
        return Result.success("修改密码成功");
    }

    /**
     * 修改个人信息
     */
    @LimitRequest
    @PutMapping("/info")
    public Result<String> updateUserInfo(@Valid @RequestBody UpdateUserInfoRequest updateUserInfoRequest,
                                          Authentication authentication) {
        String username = authentication.getName();
        userService.updateUserInfo(username, updateUserInfoRequest);
        return Result.success("修改个人信息成功");
    }

    /**
     * 注销账户
     */
    @LimitRequest
    @DeleteMapping("/account")
    public Result<String> deleteAccount(Authentication authentication) {
        String username = authentication.getName();
        userService.deleteAccount(username);
        return Result.success("注销账户成功");
    }
}
