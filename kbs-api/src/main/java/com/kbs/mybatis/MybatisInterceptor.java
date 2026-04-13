package com.kbs.mybatis;

import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * mybatis拦截器，自动注入创建人、创建时间、修改人、修改时间
 */
@Slf4j
@Component
@Intercepts({ @Signature(type = Executor.class, method = "update", args = { MappedStatement.class, Object.class }) })
public class MybatisInterceptor implements Interceptor {

	// 表公共字段
	private static final String CREATE_USER = "createUser";
	private static final String CREATE_TIME = "createTime";
	private static final String UPDATE_USER = "updateUser";
	private static final String UPDATE_TIME = "updateTime";

	// 缓存类的字段信息，避免每次反射，提升性能
	private static final Map<Class<?>, List<Field>> FIELD_CACHE = new ConcurrentHashMap<>();

	@Override
	public Object intercept(Invocation invocation) throws Throwable {

		MappedStatement mappedStatement = (MappedStatement) invocation.getArgs()[0];

		SqlCommandType sqlCommandType = mappedStatement.getSqlCommandType();
		Object parameter = invocation.getArgs()[1];
		if (parameter == null) {
			return invocation.proceed();
		}

		if (SqlCommandType.INSERT == sqlCommandType) {
			insertFieldSet(parameter);
		}

		if (SqlCommandType.UPDATE == sqlCommandType) {
			updateFieldSet(parameter);
		}

		return invocation.proceed();
	}

	@Override
	public Object plugin(Object target) {
		return Plugin.wrap(target, this);
	}

	/**
	 * 更新字段设置
	 * @param parameter
	 * @throws IllegalAccessException
	 */
	private void updateFieldSet(Object parameter) throws IllegalAccessException {

		String username = getUsername();
		List<Field> fields = getAllFields(parameter);

		for (Field field : fields) {
			String fieldName = field.getName();
			if (UPDATE_USER.equals(fieldName)) {
				field.setAccessible(true);
				field.set(parameter, username);
			} else if (UPDATE_TIME.equals(fieldName)) {
				field.setAccessible(true);
				field.set(parameter, LocalDateTime.now());
			}
		}
	}

	/**
	 * 插入字段设置
	 * @param parameter
	 * @throws IllegalAccessException
	 */
	private void insertFieldSet(Object parameter) throws IllegalAccessException {

		String username = getUsername();
		List<Field> fields = getAllFields(parameter);

		for (Field field : fields) {
			String fieldName = field.getName();
			if (CREATE_USER.equals(fieldName)) {
				setIfAbsentField(field, parameter, username);
			} else if (CREATE_TIME.equals(fieldName)) {
				setIfAbsentField(field, parameter, LocalDateTime.now());
			}
		}
	}

	/**
	 * 如果字段为空则设置字段值
	 * @param field
	 * @param parameter
	 * @param value
	 * @throws IllegalAccessException
	 */
	private void setIfAbsentField(Field field, Object parameter, Object value) throws IllegalAccessException {

		field.setAccessible(true);
		Object existValue = field.get(parameter);

		if (existValue == null) {
			field.set(parameter, value);
		}
	}

	/**
	 * 从Spring security上下文中获取当前登录用户名
	 * @return
	 */
	private String getUsername() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		return authentication != null ? authentication.getName() : null;
	}

	/**
	 * 获取对象所有字段
	 * @param object
	 * @return
	 */
	private static List<Field> getAllFields(Object object) {

		Class<?> clazz = object.getClass();

		return FIELD_CACHE.computeIfAbsent(clazz, k -> {

			List<Field> fieldList = new ArrayList<>();
			Class<?> currentClass = k;

			// 遍历当前类及所有父类
			while (currentClass != null) {
				fieldList.addAll(Arrays.asList(currentClass.getDeclaredFields()));
				currentClass = currentClass.getSuperclass();
			}

			return fieldList;
		});
	}

}
