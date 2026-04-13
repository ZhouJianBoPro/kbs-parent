package com.kbs.common.consts;

/**
 * @Desc:
 * @Author: zhoujianbo
 * @Version: 1.0
 * @Date: 2026/4/8 11:44
 **/
public class ResultCodeConstant {

    // 成功
    public static final Integer SUCCESS = 200;

    // 失败
    public static final Integer ERROR = 500;

    // 未认证（自动跳转登录页面）
    public static final Integer UN_AUTHENTICATION = 401;

    // access token 过期
    public static final Integer ACCESS_AUTH_EXPIRE = 402;

    // 未授权
    public static final Integer UN_AUTHORIZED = 403;
}
