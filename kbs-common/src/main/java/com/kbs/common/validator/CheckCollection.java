package com.kbs.common.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Desc: 校验集合
 * @Author: zhoujianbo
 * @Version: 1.0
 * @Date: 2024/9/4 15:36
 **/
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {CheckCollectionValidator.class})
@Target(ElementType.FIELD)
public @interface CheckCollection {

    String message();

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    int max() default Integer.MAX_VALUE;
}
