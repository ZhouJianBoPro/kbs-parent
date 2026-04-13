package com.kbs.common.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * 用户状态枚举
 */
@Getter
public enum UserStatus {

    /**
     * 正常
     */
    NORMAL(0, "正常"),

    /**
     * 禁用
     */
    DISABLED(1, "禁用");

    @JsonValue
    private final Integer code;

    private final String desc;

    UserStatus(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
