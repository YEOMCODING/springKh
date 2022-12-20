package com.kh.spring.common.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@WebFilter("/*")
public class InitFilter implements Filter {

    private Logger logger = LoggerFactory.getLogger(InitFilter.class);

    public void init(FilterConfig config) throws ServletException {
        logger.info("초기화 필터 생성");
    }

    public void destroy() {
        logger.info("초기화 필터 종료");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws ServletException, IOException {

        // application 내장 객체 얻어오기
        ServletContext application = ((HttpServletRequest) request).getSession().getServletContext();

        // 모든 영역에서 spring 을 사용하려함
        // application scope - session scope - request scope - page scope

        String contextPath = ((HttpServletRequest) request).getContextPath();

        application.setAttribute("contextPath",contextPath);

        chain.doFilter(request, response);
    }
}
