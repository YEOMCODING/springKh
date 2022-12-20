package com.kh.spring;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class MainController {
	
	@RequestMapping("/")
	public String home(HttpSession session, Model model, HttpServletResponse res) {
		
		return "main";
	}

	
	
	
	
	
	
	
	
	
}
