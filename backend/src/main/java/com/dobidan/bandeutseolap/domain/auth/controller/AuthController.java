package com.dobidan.bandeutseolap.domain.auth.controller;

import com.dobidan.bandeutseolap.domain.auth.dto.LoginRequest;
import com.dobidan.bandeutseolap.domain.auth.dto.LoginResponse;
import com.dobidan.bandeutseolap.domain.auth.dto.SignupRequest;
import com.dobidan.bandeutseolap.domain.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

/**
 * AuthController
 *
 * - 회원가입, 로그인, 로그아웃 요청을 처리하는 인증 관련 컨트롤러.
 * - 비즈니스 로직은 AuthService에 위임한다.
 */

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * 회원가입 API - POST /auth/signup
     *
     * - username 중복 체크
     * - 비밀번호 암호화 후 DB 저장
     */
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody SignupRequest request) {
        authService.signup(request);
        return ResponseEntity.ok("회원가입 완료");
    }

    /**
     * 로그인 API - POST /auth/login
     *
     * - 아이디/비밀번호 검증
     * - Access Token + Refresh Token 발급
     * - Refresh Token은 Redis에 저장
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    /**
     * 로그아웃 API - POST /auth/logout
     *
     * - 인증된 사용자의 Refresh Token을 Redis에서 삭제
     */
    @PostMapping("/logout")
    public ResponseEntity<?> logout(@AuthenticationPrincipal UserDetails userDetails) {
        authService.logout(userDetails.getUsername());
        return ResponseEntity.ok("로그아웃 완료");
    }
}

