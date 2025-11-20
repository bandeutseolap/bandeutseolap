package com.dobidan.bandeutseolap.domain.auth.controller;

import com.dobidan.bandeutseolap.domain.auth.dto.LoginRequest;
import com.dobidan.bandeutseolap.domain.auth.dto.LoginResponse;
import com.dobidan.bandeutseolap.domain.auth.dto.SignupRequest;
import com.dobidan.bandeutseolap.domain.user.entity.User;
import com.dobidan.bandeutseolap.domain.user.repository.UserRepository;
import com.dobidan.bandeutseolap.global.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

/**
 * AuthController
 *
 * - 회원가입(/auth/signup)
 * - 로그인(/auth/login)
 * 을 담당하는 인증 관련 컨트롤러.
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * 회원가입 API
     *
     * 1. username 중복 체크
     * 2. 비밀번호 암호화
     * 3. DB에 User 저장
     */
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody SignupRequest request) {

        // 1. username 중복 체크
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            return ResponseEntity.badRequest().body("이미 존재하는 사용자입니다.");
        }

        // 2. 비밀번호 암호화 후 User 엔티티 생성
        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword())) // 비밀번호 암호화
                .role("USER") // 기본 권한 USER
                .build();

        // 3. DB 저장
        userRepository.save(user);

        return ResponseEntity.ok("회원가입 완료");
    }

    /**
     * 로그인 API
     *
     * 1. AuthenticationManager를 통해 아이디/비밀번호 검증
     * 2. 인증 성공 시 JWT 토큰 생성
     * 3. Access Token 응답으로 반환
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {

        // 1. 아이디/비밀번호 검증 (Spring Security가 내부에서 UserDetailsService + PasswordEncoder 사용)
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        // 2. 인증 정보에서 username, 권한을 꺼내 JWT 생성
        String token = jwtTokenProvider.createToken(
                authentication.getName(),              // username
                authentication.getAuthorities()        // roles
        );

        // 3. 토큰을 응답으로 반환
        return ResponseEntity.ok(new LoginResponse(token));
    }
}
