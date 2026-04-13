package com.kbs.exception;

import com.kbs.common.consts.ResultCodeConstant;
import com.kbs.common.exception.BusinessException;
import com.kbs.common.model.Result;
import com.kbs.security.starter.exception.CustomAuthenticationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 全局异常处理器
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(BusinessException.class)
	public Result<?> handleBusinessException(BusinessException e){
		log.error("业务处理异常", e);
		return Result.error(e.getMessage());
	}

	@ExceptionHandler(CustomAuthenticationException.class)
	public Result<?> handleAuthorizationException(CustomAuthenticationException e){
		return Result.error(e.getCode(), e.getMessage());
	}

	@ExceptionHandler(AuthorizationDeniedException.class)
	public Result<?> handleAuthorizationDeniedException(AuthorizationDeniedException e){
		return Result.error(ResultCodeConstant.UN_AUTHORIZED, "无权限访问该资源");
	}

	@ExceptionHandler(Exception.class)
	public Result<?> handleException(Exception e){
		log.error("系统异常", e);
		return Result.error();
	}

	@ExceptionHandler(BindException.class)
	public Result<?> handleBindException(BindException e){
		log.error("参数绑定异常", e);
		List<String> errorMsg = e.getBindingResult().getAllErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage).collect(Collectors.toList());
		return Result.error(String.join(",", errorMsg));
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<?> handlerMethodArgumentNotValidException(MethodArgumentNotValidException e) {
		log.error("参数校验未通过", e);
		List<String> errorMsg = e.getBindingResult().getFieldErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage).collect(Collectors.toList());
        return Result.error(String.join(",", errorMsg));
    }

	@ExceptionHandler(ConstraintViolationException.class)
	public Result<?> handleConstraintViolationException(ConstraintViolationException e){
		log.error("参数校验未通过", e);
		List<String> errorMsg = e.getConstraintViolations().stream().map(ConstraintViolation::getMessageTemplate).collect(Collectors.toList());
		return Result.error(String.join(",", errorMsg));
	}

}
