package com.kbs.api.dto;

import lombok.Data;

/**
 * 修改个人信息请求DTO
 */
@Data
public class UpdateUserInfoRequest {

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 邮箱
     */
    private String email;
}
