package com.kh.spring.common.interceptor;

import com.kh.spring.member.model.vo.Member;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Map;

public class LoggingInterceptor extends HandlerInterceptorAdapter {
    static Logger logger = LoggerFactory.getLogger(LoggingInterceptor.class);
    // 사용자가 사용하고 있는 핸드폰 종류
    static String logMP[] = {"iphone","ipod","android","blackberry","opera mobi"};

    /*
        모든 경로로 들어오는 요청에 대한 로그정보를 남기기 위한 메소드
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 접속된 장비가 무엇인지(웹인지 모바일인지)
        String currentDevice = "web";
        String logUA = request.getHeader("user-agent").toLowerCase();
        for(String device : logMP) {
            if (logUA.indexOf(device) > 0) {
                currentDevice = "mobile";
                break;
            }
        }
        // 사용자의 접속 url , 서버정보 추가
        //String requestUrl = request.getRequestURI().substring(request.getContextPath().length());

        HttpSession session = request.getSession();
        String currentDomain = request.getServerName();
        int currentPort = request.getServerPort();
        String queryString = "";

        if (request.getMethod().equals("GET")) {
            queryString = request.getQueryString();
        }else {
            Map map = request.getParameterMap();
            Object[] keys = map.keySet().toArray();
            for (int i = 0; i < keys.length; i++) {
                if (i>0){
                    queryString += "&";
                }
                String [] values = (String [])map.get(keys[i]); // 값이 여러개가 넘어올 수 있으므로 강제 형변환 필요
                queryString += keys[i] + "=";
                int count = 0;
                for (String str : values) {
                    if(count>0) {
                        queryString += ",";
                    }
                    queryString += str;
                    count++; // value값이 여러개 있을수도 있으니까
                }
            }
        }
        // 파라미터가 아예 없다면 아예 로그에 포함시키지 않을 예정
        if(queryString == null || queryString.trim().length() == 0) {
            queryString = null;
        }

        // 아이디 정보 추가
        String userId = "";
        Member user = (Member)session.getAttribute("loginUser");
        if(user != null) {
            userId = user.getUserId();
        }

        // ip정보 추가
        String uri = request.getRequestURI();
        //String ip = request.getHeader("X-Forwarded-For");
        String ip = getIp(request);

        // 프로토콜 정보 추가
        String protocol = (request.isSecure()) ? "https" : "http";

        logger.info(ip + " : " + currentDevice + " : " + userId + " : " + protocol + " ://" + currentDomain + currentPort + uri +
                (queryString != null ? "?" + queryString : ""));
        return true;
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
