package com.kbs.common.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

/**
 * @Desc: 参数条件必填校验，内嵌容器注解（容器注解执行主注解的校验逻辑），支持多个字段条件必填校验
 * @Author: zhoujianbo
 * @Version: 1.0
 * @Date: 2025/12/19 10:58
 **/
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {ConditionalRequiredValidator.class})
@Repeatable(ConditionalRequired.List.class)
public @interface ConditionalRequired {

    String message();

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    /**
     * 条件字段
     * @return
     */
    String conditionalField();

    /**
     * 条件值
     * @return
     */
    String conditionalValue();

    /**
     * 需要校验的字段
     * @return
     */
    String checkField();

    /**
     * 容器注解，用于配置多个ConditionalRequired规则
     */
    @Target({ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @interface List {
        ConditionalRequired[] value();
    }

}
