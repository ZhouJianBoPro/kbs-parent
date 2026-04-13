package com.kbs.common.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Collection;

/**
 * @Desc: 集合校验器
 * @Author: zhoujianbo
 * @Version: 1.0
 * @Date: 2024/9/4 15:34
 **/
public class CheckCollectionValidator implements ConstraintValidator<CheckCollection, Collection> {

    private Integer max;

    @Override
    public void initialize(CheckCollection constraintAnnotation) {
        this.max = constraintAnnotation.max();
    }

    @Override
    public boolean isValid(Collection s, ConstraintValidatorContext constraintValidatorContext) {
        return s != null && !s.isEmpty() && s.size() <= max;
    }
}
