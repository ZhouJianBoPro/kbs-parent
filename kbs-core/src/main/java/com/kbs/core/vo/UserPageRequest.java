package com.kbs.core.vo;

import com.kbs.common.entity.BasePage;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户分页查询请求VO
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class UserPageRequest extends BasePage {

    /**
     * 用户名（模糊查询）
     */
    private String username;

    /**
     * 昵称（模糊查询）
     */
    private String nickname;

    /**
     * 手机号（模糊查询）
     */
    private String phone;

    /**
     * 状态（0-正常，1-禁用）
     */
    private Integer status;
}
