package com.dobidan.bandeutseolap.domain.auth.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * MeController
 *
 * - 현재 로그인한 사용자 정보를 확인하기 위한 테스트용 컨트롤러.
 * - JWT 인증이 잘 동작하는지 확인하는 용도.
 */
@RestController
public class MeController {

    @GetMapping("/me")
    public String me(@AuthenticationPrincipal UserDetails userDetails){
        return "안녕하세요!" + userDetails.getUsername();
    }

}
