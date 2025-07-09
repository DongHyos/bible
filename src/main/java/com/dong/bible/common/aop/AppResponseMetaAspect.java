package com.dong.bible.common.aop;

import com.dong.bible.common.AppProperties;
import com.dong.bible.common.response.AppResponse;
import com.dong.bible.common.response.ResMeta;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Objects;

@Slf4j
@Order(2)
@Aspect
@Component
@ConditionalOnProperty(name = "app.option.meta", havingValue = "true")
@RequiredArgsConstructor
public class AppResponseMetaAspect {

    private final AppProperties properties;

    @Around("within(@org.springframework.web.bind.annotation.RestController *)")
    public Object proceed(ProceedingJoinPoint joinPoint) throws Throwable {
        HttpServletRequest req = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();

        LocalDateTime requestDateTime = LocalDateTime.now();
        Object result = joinPoint.proceed();
        Duration duration = Duration.between(requestDateTime, LocalDateTime.now());
        if(result instanceof ResponseEntity){
            ResponseEntity<?> responseEntity = (ResponseEntity<?>)result;

            if(responseEntity.getBody() instanceof AppResponse) {
                AppResponse appResponse = (AppResponse) responseEntity.getBody();
                appResponse.setMeta(ResMeta.builder()
                                .path(req.getRequestURI())
                                .timeTaken(LocalTime.ofNanoOfDay(duration.getNano()))
                                .timestamp(requestDateTime)
                        .build());
            }
        }
        return result;
    }
}
