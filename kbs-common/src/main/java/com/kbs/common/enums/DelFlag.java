package com.kbs.common.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * 删除标志枚举
 */
@Getter
public enum DelFlag {

    /**
     * 未删除
     */
    NOT_DELETED(0, "未删除"),

    /**
     * 已删除
     */
    DELETED(1, "已删除");

    @JsonValue
    private final Integer code;

    private final String desc;

    DelFlag(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
