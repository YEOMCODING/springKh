package com.kh.spring.common.aop;

import com.kh.spring.member.model.vo.Member;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;

@Component
@Aspect
public class BeforeTest {

    private Logger logger = LoggerFactory.getLogger(BeforeTest.class);

    static String logMP[] = {"iphone","ipod","android","blackberry","opera mobi"};

    // joinpoint : advice 가 적용될수 있는 후보

    // JoinPoint : advice 가 실제로 적용되는 Target Object 의
    //              정보, 전달되는 매개변수, 메서드, 반환값, 예외등의 정보를 얻을수 있는 메서드 제공.

    // (주의사항) JoinPoint 인터페이스는 항상 첫번째 매개변수로 작성 되어야 한다.
    @Before("CommonPointcut.implPointcut()")
    public void logService(JoinPoint jp){

        StringBuilder sb = new StringBuilder();
        sb.append("==================================================\n");
        // start : 실행된 클래스명 - 실행된 메서드
        // joinPoint : getTarget() -> aop가 적용된 개체를 반환(ServiceImpl)
        sb.append("start : " + jp.getTarget().getClass().getSimpleName()+"-");  // 패키지명을 제외한 클래스명

        // JoinPoint.getSignature() : 수행되는 메서드의 정보
        sb.append(jp.getSignature().getName());

        // JoinPoint : getArgs() : 메서드호출시 전달된 매개변수
        sb.append("(" + Arrays.toString(jp.getArgs()) + ")\n");

        // JoinPoint : getArgs() : 메서드 호출시 전달된 매개변수


        // ip : xxx.xxxx.....

        // HttpServletRequest 객체 얻어오기
        // 스케쥴러에서 예외발생 -> 예외처리해줘야 함
        try{
            HttpServletRequest req = ((ServletRequestAttributes)RequestContextHolder.currentRequestAttributes()).getRequest();

            String currentDevice = "web";
            String logUA = req.getHeader("user-agent").toLowerCase();
            for(String device : logMP) {
                if (logUA.indexOf(device) > 0) {
                    currentDevice = "mobile";
                    break;
                }
            }
            // http: // domain:port + uri

            // http + domain + req.getServerPort() + uri
            sb.append("ip : " + currentDevice + getIp(req)+" "+(req.isSecure()?"https":"http")+"://"
                        +req.getServerName()+req.getServerPort()+req.getRequestURI());
            // userId : 유저아이디
            Member loginUser = (Member) (req.getSession().getAttribute("loginUser"));
            if(loginUser != null){
                sb.append("\nuserId : " + loginUser.getUserId());
            }
            logger.info(sb.toString());

        }catch (Exception e){
            sb.append("스케쥴러 예외 발생");
        }


    }




    public String getIp(HttpServletRequest request){
        String ip = request.getHeader("X-Forwarded-For");

        if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}
