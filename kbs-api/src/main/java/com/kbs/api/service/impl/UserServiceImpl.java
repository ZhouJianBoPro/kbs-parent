package com.kbs.api.service.impl;

import com.kbs.api.dto.RegisterRequest;
import com.kbs.api.dto.UpdatePasswordRequest;
import com.kbs.api.dto.UpdateUserInfoRequest;
import com.kbs.api.service.AppUserService;
import com.kbs.api.service.AuthService;
import com.kbs.api.service.UserService;
import com.kbs.api.vo.UserInfoVO;
import com.kbs.common.enums.AccountType;
import com.kbs.common.enums.DelFlag;
import com.kbs.common.enums.UserStatus;
import com.kbs.common.exception.BusinessException;
import com.kbs.core.entity.AppUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * 用户服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final AppUserService appUserService;
    private final PasswordEncoder passwordEncoder;
    private final AuthService authService;

    @Override
    public String register(RegisterRequest registerRequest) {
        // 检查用户名是否存在
        AppUser existUser = appUserService.getByUsername(registerRequest.getUsername());
        if (existUser != null) {
            throw new BusinessException("用户名已存在");
        }

        // 创建用户
        AppUser appUser = new AppUser();
        appUser.setUsername(registerRequest.getUsername());
        appUser.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        appUser.setNickname(registerRequest.getNickname());
        appUser.setPhone(registerRequest.getPhone());
        appUser.setEmail(registerRequest.getEmail());
        appUser.setStatus(UserStatus.NORMAL.getCode());
        appUser.setAccountType(AccountType.NORMAL.name());

        appUserService.save(appUser);

        log.info("用户注册成功: {}", registerRequest.getUsername());
        return appUser.getId();
    }

    @Override
    public UserInfoVO getUserInfo(String username) {
        AppUser appUser = appUserService.getByUsername(username);
        if (appUser == null) {
            throw new BusinessException("用户不存在");
        }

        return convertToVO(appUser);
    }

    @Override
    public void updatePassword(String username, UpdatePasswordRequest updatePasswordRequest) {
        AppUser appUser = appUserService.getByUsername(username);
        if (appUser == null) {
            throw new BusinessException("用户不存在");
        }

        // 验证旧密码
        if (!passwordEncoder.matches(updatePasswordRequest.getOldPassword(), appUser.getPassword())) {
            throw new BusinessException("旧密码错误");
        }

        // 更新密码
        appUser.setPassword(passwordEncoder.encode(updatePasswordRequest.getNewPassword()));
        boolean updated = appUserService.updateById(appUser);
        if (!updated) {
            throw new BusinessException("修改密码失败");
        }

        // 退出登录
        authService.logout(username);

        log.info("用户修改密码成功: {}", username);
    }

    @Override
    public void updateUserInfo(String username, UpdateUserInfoRequest updateUserInfoRequest) {
        AppUser appUser = appUserService.getByUsername(username);
        if (appUser == null) {
            throw new BusinessException("用户不存在");
        }

        appUser.setNickname(updateUserInfoRequest.getNickname());
        appUser.setPhone(updateUserInfoRequest.getPhone());
        appUser.setEmail(updateUserInfoRequest.getEmail());

        boolean updated = appUserService.updateById(appUser);
        if (!updated) {
            throw new BusinessException("修改个人信息失败");
        }

        log.info("用户修改个人信息成功: {}", username);
    }

    @Override
    public void deleteAccount(String username) {
        AppUser appUser = appUserService.getByUsername(username);
        if (appUser == null) {
            throw new BusinessException("用户不存在");
        }

        // 逻辑删除
        appUser.setDelFlag(DelFlag.DELETED.getCode());
        appUserService.updateById(appUser);

        log.info("用户注销账户成功: {}", username);
    }

    /**
     * 转换为VO
     */
    private UserInfoVO convertToVO(AppUser appUser) {
        UserInfoVO vo = new UserInfoVO();
        vo.setId(appUser.getId());
        vo.setAccountType(appUser.getAccountType());
        vo.setUsername(appUser.getUsername());
        vo.setNickname(appUser.getNickname());
        vo.setPhone(appUser.getPhone());
        vo.setEmail(appUser.getEmail());
        vo.setRemark(appUser.getRemark());
        vo.setCreateTime(appUser.getCreateTime());
        return vo;
    }
}
