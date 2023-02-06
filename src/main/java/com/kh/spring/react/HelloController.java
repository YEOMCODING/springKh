package com.kh.spring.react;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
// json 타입의 반환만 모아두는 controller
public class HelloController {

    @GetMapping("/test")
    public List<String> Hello(){
        String springServerPort = "8083";
        String reactServerPort = "3000";
        return Arrays.asList(springServerPort,reactServerPort); // 배열을 list 형태로 반환해주는 메서드
    }
}
