package com.kh.spring.common;

import com.kh.spring.member.model.vo.Member;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public class AccessValidator extends HandlerInterceptorAdapter {

    @Override
    public boolean preHandle(HttpServletRequest req, HttpServletResponse res, Object handler){

        // 요청 url 정보 spring/board/list.bo -> board/list.bo 만 얻어오기
        String requestUrl = req.getRequestURI().substring(req.getContextPath().length());

        // 권한체크
        String role = getGrade(req.getSession()); // session 에 있는 member객체의 role을 가져온다

        // 로그인 안한 사용자
        if(role == null){
            try {
                req.setAttribute("errorMsg","로그인 후 이용할 수 있습니다.");
                req.getRequestDispatcher("/WEB-INF/views/common/errorPage.jsp").forward(req,res);
            } catch (ServletException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return false;
        }
        // 로그인 했지만 권한이 없는 사용자
        if(requestUrl.indexOf("admin") > -1 && !role.equals("A")){
            // url 에 admin 이 있는 경로로 갈 경우 session에 있는 member객체에서 role을 검사하기.ㄴ
            try {
                req.setAttribute("errorMsg","권한이 없습니다");
                req.getRequestDispatcher("/WEB-INF/views/common/errorPage.jsp").forward(req,res);
            } catch (ServletException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return  false;
        }

        return true;
    }

    public String getGrade(HttpSession session){

        Member member = (Member)session.getAttribute("loginUser");
        if(member == null){
            return null;
        }
        return member.getRole();
    }
}
