package com.dobidan.bandeutseolap.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloWorldController {

    @GetMapping("/api/hello")
    public String hello() {
        return "최종 대소문자·패키지 경로 충돌 해결 확인용 디버그";
    }
}
