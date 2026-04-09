package com.bazaarhub.backend.shared.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class ServiceLoggingAspect {

    @Around("execution(* com.bazaarhub.backend.feature..service.impl..*(..))")
    public Object logServiceExecution(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();

        String className = joinPoint.getSignature().getDeclaringTypeName();
        String methodName = joinPoint.getSignature().getName();

        log.info("Service method started: {}.{}", className, methodName);

        try {
            Object result = joinPoint.proceed();
            long end = System.currentTimeMillis();

            log.info("Service method completed: {}.{} | Execution time: {} ms",
                    className, methodName, (end - start));

            return result;
        } catch (Exception ex) {
            long end = System.currentTimeMillis();

            log.error("Service method failed: {}.{} | Execution time: {} ms | Error: {}",
                    className, methodName, (end - start), ex.getMessage());

            throw ex;
        }
    }
}