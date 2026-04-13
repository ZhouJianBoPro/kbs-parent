package com.kbs.core.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.kbs.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 应用用户实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("app_user")
public class AppUser extends BaseEntity {

    /**
     * 用户ID（雪花算法）
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 昵称
     */
    @TableField(updateStrategy = FieldStrategy.ALWAYS)
    private String nickname;

    /**
     * 手机号
     */
    @TableField(updateStrategy = FieldStrategy.ALWAYS)
    private String phone;

    /**
     * 邮箱
     */
    @TableField(updateStrategy = FieldStrategy.ALWAYS)
    private String email;

    /**
     * 状态（0-正常，1-禁用）
     */
    private Integer status;

    /**
     * 备注
     */
    private String remark;

    /**
     * 账户类型（NORMAL-普通用户，SYSTEM-系统用户）
     */
    private String accountType;
}
