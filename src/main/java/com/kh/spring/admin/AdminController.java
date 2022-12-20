package com.kh.spring.admin;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminController {
    @RequestMapping("admin.ad")
    public String admin(){
        return "board/boardListView";
    }


}
