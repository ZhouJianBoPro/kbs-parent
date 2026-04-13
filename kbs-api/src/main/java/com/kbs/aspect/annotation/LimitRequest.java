package com.kbs.aspect.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Description: 接口访问限制
 * @Author: zjb
 * @createTime: 2023年07月24日 10:20:29
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface LimitRequest {

    /**
     * 限制时间（接口未执行完成时默认拒绝其他请求时间间隔）
     * @return
     */
    int limitSeconds() default 30;
}
