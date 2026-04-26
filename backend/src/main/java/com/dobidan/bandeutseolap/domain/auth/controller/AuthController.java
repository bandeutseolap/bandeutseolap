package com.dobidan.bandeutseolap.domain.auth.controller;

import com.dobidan.bandeutseolap.domain.auth.dto.LoginRequest;
import com.dobidan.bandeutseolap.domain.auth.dto.LoginResponse;
import com.dobidan.bandeutseolap.domain.auth.dto.ReissueRequest;
import com.dobidan.bandeutseolap.domain.auth.dto.SignupRequest;
import com.dobidan.bandeutseolap.domain.auth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
@Tag(name = "Auth", description = "인증 관련 API (회원가입, 로그인, 로그아웃, 토큰 재발급)")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "회원가입", description = "아이디 중복 체크 후 비밀번호 암호화하여 저장")
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@Valid @RequestBody SignupRequest request) {
        authService.signup(request);
        return ResponseEntity.ok("회원가입 완료");
    }

    @Operation(summary = "로그인", description = "아이디/비밀번호 검증 후 Access Token + Refresh Token 발급")
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request, HttpServletRequest httpRequest) {
        String ipAddress  = httpRequest.getRemoteAddr();
        String userAgent  = httpRequest.getHeader("User-Agent");
        String requestUrl = httpRequest.getRequestURI();
        return ResponseEntity.ok(authService.login(request, ipAddress, userAgent, requestUrl));
    }

    @Operation(summary = "로그아웃", description = "인증된 사용자의 Refresh Token을 Redis에서 삭제")
    @PostMapping("/logout")
    public ResponseEntity<String> logout(@AuthenticationPrincipal UserDetails userDetails,
                                         HttpServletRequest httpRequest) {
        log.info("로그아웃 요청 시작");
        if (userDetails == null) {
            log.warn("userDetails가 null입니다!");
            return ResponseEntity.status(401).body("인증 정보 없음");
        }
        log.info("로그아웃 요청 - username: {}", userDetails.getUsername());
        String ipAddress  = httpRequest.getRemoteAddr();
        String userAgent  = httpRequest.getHeader("User-Agent");
        String requestUrl = httpRequest.getRequestURI();
        String accessToken = httpRequest.getHeader("Authorization").substring(7).trim();
        authService.logout(userDetails.getUsername(), ipAddress, accessToken, userAgent, requestUrl);
        return ResponseEntity.ok("로그아웃 완료");
    }

    @Operation(summary = "토큰 재발급", description = "Refresh Token 검증 후 새 Access Token 발급")
    @PostMapping("/reissue")
    public ResponseEntity<LoginResponse> reissue(@RequestBody ReissueRequest request) {
        return ResponseEntity.ok(authService.reissue(request.getRefreshToken()));
    }

    @Operation(summary = "탈퇴", description = "계정 Soft Delete 처리 및 Refresh Token 삭제")
    @DeleteMapping("/withdraw")
    public ResponseEntity<String> withdraw(@AuthenticationPrincipal UserDetails userDetails) {
        authService.withdraw(userDetails.getUsername());
        return ResponseEntity.ok("회원탈퇴 완료되었습니다.");
    }
}