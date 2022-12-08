package com.jejuro.server2.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

@Slf4j
@Aspect
@Component
public class TimerAop {

    @Pointcut("@annotation(com.jejuro.server2.aop.Timer)") //Timer 어노테이션이 붙은 메서드에만 적용
    private void enableTimer(){}

    @Around("enableTimer()")
    public void around(ProceedingJoinPoint joinPoint) throws Throwable{ //메서드 실행시 걸린시간 측정
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        joinPoint.proceed(); //메서드가 실행되는 부분

        stopWatch.stop();
        log.info("total time : " + stopWatch.getTotalTimeSeconds());
    }
}