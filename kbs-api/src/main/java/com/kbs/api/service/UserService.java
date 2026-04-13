package com.kbs.api.service;

import com.kbs.api.dto.RegisterRequest;
import com.kbs.api.dto.UpdatePasswordRequest;
import com.kbs.api.dto.UpdateUserInfoRequest;
import com.kbs.api.vo.UserInfoVO;

/**
 * 用户服务接口
 */
public interface UserService {

    /**
     * 注册
     *
     * @param registerRequest 注册请求
     * @return 用户ID
     */
    String register(RegisterRequest registerRequest);

    /**
     * 获取当前用户信息
     *
     * @param username 用户名
     * @return 用户信息
     */
    UserInfoVO getUserInfo(String username);

    /**
     * 修改密码
     *
     * @param username            用户名
     * @param updatePasswordRequest 修改密码请求
     */
    void updatePassword(String username, UpdatePasswordRequest updatePasswordRequest);

    /**
     * 修改个人信息
     *
     * @param username              用户名
     * @param updateUserInfoRequest 修改个人信息请求
     */
    void updateUserInfo(String username, UpdateUserInfoRequest updateUserInfoRequest);

    /**
     * 注销账户
     *
     * @param username 用户名
     */
    void deleteAccount(String username);
}
