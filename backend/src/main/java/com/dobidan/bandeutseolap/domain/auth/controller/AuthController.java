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

    /**
     * 회원가입 API - POST /auth/signup
     *
     * - 아이디 중복 체크
     * - 비밀번호 암호화 후 DB 저장
     */
    @Operation(summary = "회원가입", description = "아이디 중복 체크 후 비밀번호 암호화하여 저장")
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@Valid @RequestBody SignupRequest request) {
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
    @Operation(summary = "로그인", description = "아이디/비밀번호 검증 후 Access Token + Refresh Token 발급")
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request, HttpServletRequest httpRequest) {
        String ipAddress = httpRequest.getRemoteAddr();
        return ResponseEntity.ok(authService.login(request, ipAddress));
    }

    /**
     * 로그아웃 API - POST /auth/logout
     *
     * - 인증된 사용자의 Refresh Token을 Redis에서 삭제
     */
    @Operation(summary = "로그아웃", description = "인증된 사용자의 Refresh Token을 Redis에서 삭제")
    @PostMapping("/logout")
    public ResponseEntity<String> logout(@AuthenticationPrincipal UserDetails userDetails,
                                         HttpServletRequest httpRequest) {
        log.info("로그아웃 요청 시작"); // null 체크 전에 먼저 찍기
        if (userDetails == null) {
            log.warn("userDetails가 null입니다!");
            return ResponseEntity.status(401).body("인증 정보 없음");
        }
        log.info("로그아웃 요청 - username: {}", userDetails.getUsername());
        String ipAddress = httpRequest.getRemoteAddr();
        String accessToken = httpRequest.getHeader("Authorization").substring(7).trim(); // 추가
        authService.logout(userDetails.getUsername(), ipAddress, accessToken); // accessToken 추가
        return ResponseEntity.ok("로그아웃 완료");
    }

    /**
     * 토큰 재발급 API - POST /auth/reissue
     *
     * - Refresh Token 유효성 검증
     * - Redis에 저장된 Refresh Token과 비교
     * - 새 Access Token 발급 후 반환
     */
    @Operation(summary = "토큰 재발급", description = "Refresh Token 검증 후 새 Access Token 발급")
    @PostMapping("/reissue")
    public ResponseEntity<LoginResponse> reissue(@RequestBody ReissueRequest request){
        return ResponseEntity.ok(authService.reissue(request.getRefreshToken()));
    }

    /**
     * 탈퇴 API - DELETE /auth/withdraw
     *
     * - 인증된 사용자의 계정을 Soft Delete로 처리 ( Why ? 이력 관리)
     * - Redis Refresh Token 삭제
     */
    @Operation(summary = "탈퇴", description = "계정 Soft Delete 처리 및 Refresh Token 삭제")
    @DeleteMapping("/withdraw")
    public ResponseEntity<String> withdraw(@AuthenticationPrincipal UserDetails userDetails){
        authService.withdraw(userDetails.getUsername());
        return ResponseEntity.ok("회원탈퇴 완료되었습니다.");
    }

}

