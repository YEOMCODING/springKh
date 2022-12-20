package com.kh.spring.common.aop;


import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component // 런타임시 필요한 위치에 코드를 끼워넣을수 있도록 bean으로 등록시켜줘야 한다.
@Aspect // 공통관심사 (특정 흐름사이에 끼여서 수행할 코드)가 작성된 클래스임을 명시.
        // Aspect어노테이션이 붙은 클래스에는 실행할 코드(advice) 와 pointCut이 정의되어있어야 한다.
        // advice(끼여들어서 실제로 수행할 메서드, 코드)
        // @PointCut (advice가 끼여들어서 수행될 클래스, 메서드위치등을 저의) 이작성되어 있어야 한다.
        // 공통적인 부분, 보안, 로그, 트랜잭션 처리에 대부분 사용된다.
public class Test {
    private Logger logger = LoggerFactory.getLogger(Test.class);

    // joinpoint : 클라이언트가 호출하는 모든 비지니스 메서드
    //              ex) 클래스의 인스턴스 생성시점. 메서드 호출시점. 예외발생 등 전부
    // pointcut : joinpoint 들 중에서 "실제로" advice가 끼어들어서 실행될 지점을 선

    /*
    Pointcut 작성 방법.

    @PointCut("execution([접근제한자] 반환형 패키지 + 클래스명.메서드명([매개변수]))
    Pointcut 표현식
    * : 모든 | 아무값
    .. : 하위 | 아래 (하위패키지) | 0개 이상의 매개변수

    @Before : Pointcut에 지정된 메서드가 수행되기 "전" advice 수행을 명시하는 어노테이션

    com.kh.spring.board 패키지 아래에 있는 Ipml로 끝나는 클래스의 모든 메서드에(매개변수 관계없이) 포인트컷 지정.
    "execution(* com.kh.spring.board..*Impl*.*(..))"
     */
    //@Before("execution(* com.kh.spring.board..*Impl*.*(..))")
    //@Before("testPointcut()")
    public void start(){ // 서비스 수행전에 실행되는 메서드(advice)
        logger.info("======================Service Start=====================");

    }

    // @After : Pointcut 에 지정된 메서드가 수행된 후. advice수행을 하라고 지시하는 어노테이션

    //@After("testPointcut()")
    public void end(){
        logger.info("=======================Service End=================================");
    }

    // @Pointcut 을 작성해놓은 메서드
    // -> Pointcut의 패턴이 작성되는 부분에 textPointCut()메서드 이름을 작성하면, PointCut에 정의한 패턴이 적용된다.

    @Pointcut("execution(* com.kh.spring.board..*Impl*.*(..))")
    public void testPointcut(){} // 내용 작성 x

}
