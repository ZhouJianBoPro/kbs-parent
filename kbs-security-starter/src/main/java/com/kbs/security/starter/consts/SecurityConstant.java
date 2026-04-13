package com.kbs.security.starter.consts;

/**
 * @Desc:
 * @Author: zhoujianbo
 * @Version: 1.0
 * @Date: 2026/3/23 16:54
 **/
public class SecurityConstant {

    /**
     * Token 前缀（用于HTTP请求头）
     */
    public static final String TOKEN_PREFIX = "Bearer ";

    /**
     * 请求头名称
     */
    public static final String HEADER_NAME = "Authorization";

    /**
     * Token 类型：Access
     */
    public static final String TOKEN_TYPE_ACCESS = "access";

    /**
     * Token 类型：Refresh
     */
    public static final String TOKEN_TYPE_REFRESH = "refresh";
}
