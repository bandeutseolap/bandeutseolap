package com.dobidan.bandeutseolap.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloWorldController {

    @GetMapping("/api/hello")
    public String hello() {
        return "이제 다음 스탭으로 나가아봅시다,,, 너무 늦었지요...???";
    }
}
