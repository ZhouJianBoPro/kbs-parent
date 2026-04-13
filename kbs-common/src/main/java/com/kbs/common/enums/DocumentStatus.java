package com.kbs.common.enums;

/**
 * @Desc:
 * @Author: zhoujianbo
 * @Version: 1.0
 * @Date: 2026/3/25 17:27
 **/
public enum DocumentStatus {

    PENDING(0, "待处理"),

    PROCESSING(1, "处理中"),

    COMPLETED(2, "已完成"),

    FAILED(3, "失败");

    private final int code;
    private final String description;

    DocumentStatus(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }
}
