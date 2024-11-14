package com.nextuple.postgres.config;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ReadOnlyInterception {
  @Around("@annotation(com.nextuple.postgres.config.ReaderDS)")
  public Object aroundMethod(ProceedingJoinPoint joinPoint) throws Throwable {
    try {
      ReadOnlyContext.enter();
      return joinPoint.proceed();
    } finally {
      ReadOnlyContext.exit();
    }
  }
}
