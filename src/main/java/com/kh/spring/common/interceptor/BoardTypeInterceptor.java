package com.kh.spring.common.interceptor;

import com.kh.spring.board.model.service.BoardService;
import com.kh.spring.board.model.vo.BoardType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;


public class BoardTypeInterceptor extends HandlerInterceptorAdapter {

    @Autowired
    private BoardService boardService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // application scope 에 boardTypeList를 저장.
        // 항상 저장하는것이 아리나 application scope에 boardTypeList가 없는경우에만.

        ServletContext application =  request.getSession().getServletContext();
        if(application.getAttribute("boardTypeList") == null){

            // boardService를 통해 BOARD_CD 값들을 얻어옴

            List<BoardType> boardTypeList = boardService.selectBoardTypeList();

            application.setAttribute("boardTypeList",boardTypeList);
        }
        return true;
    }

}
