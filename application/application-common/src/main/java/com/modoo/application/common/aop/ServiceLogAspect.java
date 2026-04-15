package com.modoo.application.common.aop;

import com.modoo.application.common.dto.scroll.request.ScrollRequest;
import com.modoo.common.util.ServiceLogAction;
import java.util.Arrays;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class ServiceLogAspect {

  private static final Set<String> SENSITIVE = Set.of("accessToken", "refreshToken");

  @Around("execution(* com.modoo.application..service..*(..))")
  public Object logService(ProceedingJoinPoint joinPoint) throws Throwable {
    long start = System.currentTimeMillis();
    String className = joinPoint.getSignature()
        .getDeclaringType()
        .getSimpleName();
    String methodName = joinPoint.getSignature()
        .getName();
    String params = summarizeParams(joinPoint.getArgs());

    log.info(
        "{} service={} method={} args={}",
        ServiceLogAction.START.prefix(),
        className,
        methodName,
        params
    );

    try {
      Object proceed = joinPoint.proceed();
      long duration = System.currentTimeMillis() - start;

      log.info(
          "{} service={} method={} durationMs={}",
          ServiceLogAction.END.prefix(),
          className,
          methodName,
          duration
      );

      return proceed;
    } catch (Exception e) {
      long duration = System.currentTimeMillis() - start;
      String exceptionSimpleName = e.getClass()
          .getSimpleName();

      log.info(
          "{} service={} method={} durationMs={} exception={} message={}",
          ServiceLogAction.ERR.prefix(),
          className,
          methodName,
          duration,
          exceptionSimpleName,
          e.getMessage()
      );

      throw e;
    }
  }

  private String summarizeParams(Object[] args) {
    if (Objects.isNull(args) || args.length == 0) {
      return "empty";
    }

    return Arrays.stream(args)
        .filter(this::isTargetParams)
        .map(Object::toString)
        .collect(Collectors.joining(", ", "[", "]"));
  }

  private boolean isTargetParams(Object arg) {
    boolean isRecord = arg.getClass()
        .isRecord();
    boolean isScroll = arg instanceof ScrollRequest;

    return isRecord || isScroll;
  }

}
