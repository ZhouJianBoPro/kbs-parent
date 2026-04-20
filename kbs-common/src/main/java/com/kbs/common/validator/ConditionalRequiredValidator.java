package com.kbs.common.validator;

import lombok.SneakyThrows;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Objects;

/**
 * @Desc: 条件必填校验器
 * @Author: zhoujianbo
 * @Version: 1.0
 * @Date: 2025/12/19 11:52
 **/
public class ConditionalRequiredValidator implements ConstraintValidator<ConditionalRequired, Object> {

    private String conditionalField;
    private String conditionalValue;
    private String checkField;

    @Override
    public void initialize(ConditionalRequired checkConditionalRequired) {
        this.conditionalField = checkConditionalRequired.conditionalField();
        this.conditionalValue = checkConditionalRequired.conditionalValue();
        this.checkField = checkConditionalRequired.checkField();
    }

    @SneakyThrows
    @Override
    public boolean isValid(Object object, ConstraintValidatorContext context) {

        if (object == null) {
            return true;
        }

        // 获取条件字段在对象中的值
        Object conditionalFieldValue = getFieldValue(object, conditionalField);
        // 自定义条件字段值与当前对象中的条件字段值不一致，则不进行校验
        if(Objects.isNull(conditionalFieldValue) || !Objects.equals(conditionalFieldValue, conditionalValue)) {
            return true;
        }

        // 条件字段满足自定义值时，校验必填字段
        Object checkFieldValue = getFieldValue(object, checkField);
        return !(
                Objects.isNull(checkFieldValue)
                        || (checkFieldValue instanceof String && StringUtils.isBlank((String) checkFieldValue))
                        || (checkFieldValue instanceof Collection && CollectionUtils.isEmpty((Collection<?>) checkFieldValue))
        );
    }

    private Object getFieldValue(Object value, String fieldName) throws NoSuchFieldException, IllegalAccessException {

        Field field = value.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        return field.get(value);
    }
}
