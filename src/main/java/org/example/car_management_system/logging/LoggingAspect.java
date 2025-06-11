package org.example.car_management_system.logging;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.*;

@Aspect
@Component
@Slf4j
public class LoggingAspect {

    @Around("execution(* org.example.car_management_system.controller..*(..))")
    public Object logRequestAndResponse(ProceedingJoinPoint joinPoint) throws Throwable {
        ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attr != null ? attr.getRequest() : null;

        if (request != null) {
            Enumeration<String> headersEnum = request.getHeaderNames();
            List<String> headers = headersEnum != null ? Collections.list(headersEnum) : List.of();

            log.info("🔹 HTTP {} - {}", request.getMethod(), request.getRequestURI());
            log.info("🔸 Headers: {}", headers);
            log.info("🔸 Args: {}", Arrays.toString(joinPoint.getArgs()));
        }

        Object result = joinPoint.proceed();

        log.info("✅ Response: {}", result);

        return result;
    }
}
