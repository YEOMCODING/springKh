package com.kh.spring.common.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Aspect
@Order(4)
public class AroundTest {
    private Logger logger = LoggerFactory.getLogger(AroundTest.class);

    // @Around : Before + After 합쳐놓은 어노테이션

    @Around("CommonPointcut.implPointcut()")
    public Object checkRunningTime(ProceedingJoinPoint jp)throws Throwable{

        // ProceedingJoinPoint 인터페이스 : 전/후처리 관련된 기능을 제공. 값을 얻어올수있는 메서드도 함께 제공함.

        // proceed() 메소드 호출 전 : @Before 용도로 사용할 advice
        // proceed(0 메소드 호출 후 : @After 용도로 사용할 advice
        // 메소드 마지막에 proceed()의 반환값을 리턴해줘야함.

        // Before
        long start = System.currentTimeMillis(); // 시스텀 서버시간은 ms로 나타낸 값

        Object obj = jp.proceed(); // before 와 after의 중간지점.

        long end = System.currentTimeMillis();
        logger.info("Running Time : {} ms",(end-start));

        return obj;
    }
}
