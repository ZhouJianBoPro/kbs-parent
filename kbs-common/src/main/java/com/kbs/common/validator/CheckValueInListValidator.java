package com.kbs.common.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.apache.commons.lang3.StringUtils;
import java.util.Arrays;

/**
 * @Desc: 厂区名称校验器
 * @Author: zhoujianbo
 * @Version: 1.0
 * @Date: 2024/9/4 15:34
 **/
public class CheckValueInListValidator implements ConstraintValidator<CheckValueInList, String> {

    private boolean isRequired;

    private String[] listVal;

    @Override
    public void initialize(CheckValueInList constraintAnnotation) {
        this.isRequired = constraintAnnotation.isRequired();
        this.listVal = constraintAnnotation.listVal();
    }

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {

        if(!isRequired && StringUtils.isBlank(s)) {
            return true;
        }

        return StringUtils.isNotBlank(s) && Arrays.asList(listVal).contains(s);
    }
}
