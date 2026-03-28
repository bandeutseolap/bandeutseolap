package com.dobidan.bandeutseolap.domain.auth.service;

import com.dobidan.bandeutseolap.domain.auth.dto.LoginRequest;
import com.dobidan.bandeutseolap.domain.auth.dto.LoginResponse;
import com.dobidan.bandeutseolap.domain.auth.dto.SignupRequest;
import com.dobidan.bandeutseolap.domain.user.entity.User;
import com.dobidan.bandeutseolap.domain.user.repository.UserRepository;
import com.dobidan.bandeutseolap.global.redis.RedisTokenService;
import com.dobidan.bandeutseolap.global.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * AuthService
 *
 * - 회원가입, 로그인, 로그아웃, 토큰 재발급 비즈니스 로직을 담당하는 서비스 클래스.
 * - 로그인 시 Access Token + Refresh Token 발급 후 Refresh Token은 Redis에 저장.
 * - 로그아웃 시 Redis에서 Refresh Token 삭제.
 */

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final RedisTokenService redisTokenService;
    private final UserDetailsService userDetailsService;
    private final LoginEventProducer loginEventProducer;

    // 회원가입
    public void signup(SignupRequest request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 사용자입니다.");
        }

        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .role("USER")
                .build();

        userRepository.save(user);

    }

    // 로그인
    public LoginResponse login(LoginRequest request) {

        // 1. 아이디 / 비밀번호 검증
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        String username = authentication.getName();

        // 2. Access Token + Refresh Token 발급
        String accessToken = jwtTokenProvider.createAccessToken(username, authentication.getAuthorities());
        String refreshToken = jwtTokenProvider.createRefreshToken(username);

        // 3. Refresh Token Redis에 저장
        redisTokenService.saveRefreshToken(username, refreshToken);

        return new LoginResponse(accessToken, refreshToken);
    }

    // 로그아웃

    public void logout(String username, String ipAddress, String accesstoken) {

        // 1. Refresh Token 삭제
        redisTokenService.deleteRefreshToken(username);

        // 2. 삭제한 Refresh Token 을 블랙리스트에 추가 (남은 만료시간 계산)
        long remainingExpiration = jwtTokenProvider.getRemainingExpiration(accesstoken); //accessToken의 남은 만료시간 계산
        redisTokenService.addBlacklist(accesstoken, remainingExpiration); //accessToken의 남은 만료시간 계산

        redisTokenService.isBlacklisted(username);
        loginEventProducer.sendLoginEvent(username, ipAddress, "LOGOUT");
    }


    // 토큰 재발급
    public LoginResponse reissue(String refreshToken) {


        // 1. Refresh Token 유효성 검증
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new IllegalArgumentException("유효하지않은 RefreshToken 정보입니다.");
        }

        // 2. username 정보 추출
        String username = jwtTokenProvider.getUsername(refreshToken);

        // 3. Redis에 저장된 토큰과 비교
        String savedToken = redisTokenService.getRefreshToken(username);

        if (!refreshToken.equals(savedToken)) {
            throw new IllegalArgumentException("Refresh Token이 일치하지 않습니다.");
        }

        // 4. 새 Access Token 발급
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        String newAccessToken = jwtTokenProvider.createAccessToken(username, userDetails.getAuthorities());

        return new LoginResponse(newAccessToken, refreshToken);
    }

}