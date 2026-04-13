package com.kbs.common.validator;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.ValidationException;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.constraints.NotNull;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Array;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * @Desc: 校验工具类
 * @Author: zhoujianbo
 * @Version: 1.0
 * @Date: 2024/9/9 18:48
 **/
public class ValidatorUtil {

    private static final Validator validator;

    static {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    /**
     * 实体类中注解字段校验
     * @param t
     * @param <T>
     * @throws ValidationException
     */
    public static <T> void validate(@NotNull T t) throws ValidationException {

        Set<ConstraintViolation<T>> violations = validator.validate(t);
        if(CollectionUtils.isEmpty(violations)) {
            return;
        }

        List<String> errorMsg = violations.stream().map(ConstraintViolation::getMessageTemplate).collect(Collectors.toList());
        throw new ValidationException(String.join(",", errorMsg));
    }

    /**
     * 验证对象非空
     * @param t
     * @param errorMsg
     * @param <T>
     * @throws ValidationException
     */
    public static <T> void validateNoNull(T t, String errorMsg) throws ValidationException {
        if(Objects.isNull(t)) {
            throw new ValidationException(errorMsg);
        }
    }

    /**
     * 验证对象为空
     * @param t
     * @param errorMsg
     * @param <T>
     * @throws ValidationException
     */
    public static <T> void validateIsNull(T t, String errorMsg) throws ValidationException {
        if(Objects.nonNull(t)) {
            throw new ValidationException(errorMsg);
        }
    }

    /**
     * 校验值是否在集合中
     * @param value
     * @param list
     * @param errorMsg
     * @throws ValidationException
     */
    public static void validateValueInList(String value, List<String> list, String errorMsg) throws ValidationException {
        if(!list.contains(value)) {
            throw new ValidationException(errorMsg);
        }
    }

    /**
     * 校验字符串是否相等
     * @param val1
     * @param val2
     * @param errorMsg
     * @throws ValidationException
     */
    public static void validateEquals(String val1, String val2, String errorMsg) throws ValidationException {
        if(StringUtils.isBlank(val1) || !val1.equals(val2)) {
            throw new ValidationException(errorMsg);
        }
    }

    /**
     * 验证字符串非空
     * @param val
     * @param errorMsg
     * @param <T>
     * @throws ValidationException
     */
    public static <T> void validateNotBlank(String val, String errorMsg) throws ValidationException {
        if(StringUtils.isBlank(val)) {
            throw new ValidationException(errorMsg);
        }
    }

    /**
     * 验证是否为true
     * @param val
     * @param errorMsg
     * @param <T>
     * @throws ValidationException
     */
    public static <T> void isTure(boolean val, String errorMsg) throws ValidationException {
        if(!val) {
            throw new ValidationException(errorMsg);
        }
    }

    /**
     * 验证查询非空
     * @param querySupplier
     * @param errorMsg
     * @param <T>
     * @throws ValidationException
     */
    public static <T> T validateQueryNoNull(Supplier<T> querySupplier, String errorMsg) throws ValidationException {

        T result = querySupplier.get();

        if(Objects.isNull(result)) {
            throw new ValidationException(errorMsg);
        }

        if(result instanceof Collection && ((Collection<?>) result).isEmpty()) {
            throw new ValidationException(errorMsg);
        }

        if((result.getClass().isArray() && Array.getLength(result) == 0)) {
            throw new ValidationException(errorMsg);
        }

        return result;
    }

    /**
     * 验证状态是否可编辑
     * @param status
     * @throws ValidationException
     */
    public static void validateEnableEditStatus(String status) throws ValidationException {
        if(!Arrays.asList("暂存", "已撤销", "已驳回").contains(status)) {
            throw new ValidationException("【"+ status + "】不允许此操作！");
        }
    }
}
