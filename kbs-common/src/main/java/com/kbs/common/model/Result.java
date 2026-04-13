package com.kbs.common.model;

import com.kbs.common.consts.ResultCodeConstant;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.io.Serializable;

/**
 * 通用响应结果类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Result<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    private int code;
    private String message;
    private T data;

    public static <T> Result<T> success() {
        return new Result<>(ResultCodeConstant.SUCCESS, "操作成功", null);
    }

    public static <T> Result<T> success(T data) {
        return new Result<>(ResultCodeConstant.SUCCESS, "操作成功", data);
    }

    public static <T> Result<T> success(String message, T data) {
        return new Result<>(ResultCodeConstant.SUCCESS, message, data);
    }

    public static <T> Result<T> error() {
        return new Result<>(ResultCodeConstant.ERROR, "操作失败", null);
    }

    public static <T> Result<T> error(String message) {
        return new Result<>(ResultCodeConstant.ERROR, message, null);
    }

    public static <T> Result<T> error(int code, String message) {
        return new Result<>(code, message, null);
    }

    public static <T> Result<T> error(int code, String message, T data) {
        return new Result<>(code, message, data);
    }
}