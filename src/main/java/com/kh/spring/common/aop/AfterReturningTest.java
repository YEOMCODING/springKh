package com.kh.spring.common.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Aspect
@Order(5) // advice 의 실행순서를 결정. 클수록 먼저 시작됨.
public class AfterReturningTest {

    private Logger logger = LoggerFactory.getLogger(AfterReturning.class);

    // @AfterReturning : 메서드 실행이후에 반환값을 얻어오는 기능의 어노테이션
    // returning : 반환값을 저장할 매개변수명을 지정하는 속성.
    @AfterReturning(pointcut = "CommonPointcut.implPointcut()",returning = "returnObj")
    public void returnValue(JoinPoint jp, Object returnObj){
        logger.info("return value : {}",returnObj);
    }

}
