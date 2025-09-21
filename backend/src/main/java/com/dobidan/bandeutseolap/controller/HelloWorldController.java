package com.dobidan.bandeutseolap.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloWorldController {

    @GetMapping("/api/hello")
    public String hello() {
        return "커밋반영이잘될까요?";
    }
}
