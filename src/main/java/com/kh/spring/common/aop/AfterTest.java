package com.kh.spring.common.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Aspect
@Order(1) // 실행될 순서 , 숫자가 적을수록 후에 실행된다.
public class AfterTest {

    private Logger logger = LoggerFactory.getLogger(AfterTest.class);

    @After("CommonPointcut.implPointcut()")
    public void serviceEnd(JoinPoint jp){

        StringBuilder sb = new StringBuilder();

        sb.append("end : "+jp.getTarget().getClass().getSimpleName()+" - "); // 간단 클래스명
        sb.append(jp.getSignature().getName());
        sb.append("\n==================================================\n");
        logger.info(sb.toString());


    }


}
