package com.kbs.aspect;

import com.kbs.aspect.annotation.LimitRequest;
import com.kbs.common.consts.RedisConstant;
import com.kbs.common.exception.BusinessException;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

/**
 * @Description: 接口访问限制切面
 * @Author: zjb
 * @createTime: 2023年07月24日 10:27:16
 */
@Slf4j
@Aspect
@Component
public class LimitRequestAspect {

    @Resource
    private RedissonClient redissonClient;

    @Pointcut("@annotation(com.kbs.aspect.annotation.LimitRequest)")
    public void pointcut() {}

    @Around("pointcut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {

        // 获取方法签名
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        String methodFullName = signature.getDeclaringTypeName() + "." + signature.getName();
        String methodLockKey = RedisConstant.LOCK_METHOD + username + ":" + methodFullName;

        // 获取方法上指定注解
        Method method = signature.getMethod();
        LimitRequest limitRequest = method.getAnnotation(LimitRequest.class);
        int limitSeconds = limitRequest.limitSeconds();

        RLock rLock = redissonClient.getLock(methodLockKey);
        boolean isLocked = rLock.tryLock(0, limitSeconds, TimeUnit.SECONDS);

        if(!isLocked) {
            throw new BusinessException("操作正在执行, 请稍后重试");
        }

        try {
            return joinPoint.proceed();
        } finally {
            unlock(rLock);
        }
    }

    /**
     * 释放锁有可能出现异常，比如锁设置的ttl在业务逻辑执行完失效了
     * @param rLock
     */
    private void unlock(RLock rLock) {

        try {
            rLock.unlock();
        } catch (Exception e) {
            log.error("release method lock error", e);
        }
    }

}
