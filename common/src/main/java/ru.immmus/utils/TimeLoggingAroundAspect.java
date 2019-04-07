package ru.immmus.utils;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@Slf4j
@Aspect
public class TimeLoggingAroundAspect {

    @Around("execution(* ru.immmus.controller.MessageController.*(..))")
    public Object log(ProceedingJoinPoint pjp) throws Throwable {
        LocalDateTime start =LocalDateTime.now();

        Throwable toThrow = null;
        Object returnValue = null;

        try {
            returnValue = pjp.proceed();
        } catch (Throwable throwable) {
            toThrow = throwable;
        }
        LocalDateTime stop = LocalDateTime.now();
        log.info("starting @ " + start.toString());
        log.info("finishing @ {} with duration {} ms",
                stop.toString(),
                (stop.minusNanos(start.getNano()).getNano()) / 1000_000);
        if (null != toThrow) throw toThrow;

        return returnValue;
    }
}
