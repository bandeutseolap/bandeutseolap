package com.dobidan.bandeutseolap.domain.auth.service;

import com.dobidan.bandeutseolap.domain.auth.dto.LoginRequest;
import com.dobidan.bandeutseolap.domain.auth.dto.LoginResponse;
import com.dobidan.bandeutseolap.domain.auth.dto.SignupRequest;
import com.dobidan.bandeutseolap.domain.user.entity.AppUser;
import com.dobidan.bandeutseolap.domain.user.entity.AppUserInfo;
import com.dobidan.bandeutseolap.domain.user.repository.AppUserInfoRepository;
import com.dobidan.bandeutseolap.domain.user.repository.AppUserRepository;
import com.dobidan.bandeutseolap.global.kafka.LoginEventProducer;
import com.dobidan.bandeutseolap.global.redis.RedisTokenService;
import com.dobidan.bandeutseolap.global.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.cglib.core.Local;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

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

    private final AppUserRepository appUserRepository;
    private final AppUserInfoRepository appUserInfoRepository;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final RedisTokenService redisTokenService;
    private final UserDetailsService userDetailsService;
    private final LoginEventProducer loginEventProducer;

    // 회원가입
    public void signup(SignupRequest request) {

        // 1. 로그인 아이디 중복체크
        if(appUserRepository.existsByLoginId(request.getLoginId())){
            throw new IllegalArgumentException("이미 존재하는 아이디입니다.");
        }

        // 2. 이메일 중복체크
        if (appUserRepository.existsByEmail(request.getEmail())){
            throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
        }

        // 3.AppUser 객체
        AppUser savedUser = appUserRepository.save(AppUser.builder()
                .loginId(request.getLoginId())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .userName(request.getUserName())
                .email(request.getEmail())
                .mobilePhone(request.getMobilePhone())
                .build());

        // 4. AppUserInfo 객체
        appUserInfoRepository.save(AppUserInfo.builder()
                .appUser(savedUser)
                .birthDt(request.getBirthDt())
                .jobCd(request.getJobCd())
                .countryCd(request.getCountryCd())
                .imagePath(request.getImagePath())
                .build());

    }

    // 로그인
    public LoginResponse login(LoginRequest request,String ipAddress) {

        // 1. 아이디 / 비밀번호 검증
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getLoginId(),
                        request.getPassword()
                )
        );

        String username = authentication.getName();

        // 2. 마지막 로그인 시점 업데이트
        AppUser appuser = appUserRepository.findByLoginId(username)
                .orElseThrow(()-> new IllegalArgumentException("존재하지 않는 유저입니다."));
        appuser.setLastLgnAt(LocalDateTime.now());
        appUserRepository.save(appuser);

        // 3. Access Token + Refresh Token 발급
        String accessToken = jwtTokenProvider.createAccessToken(username, authentication.getAuthorities());
        String refreshToken = jwtTokenProvider.createRefreshToken(username);

        // 4. Refresh Token Redis에 저장
        redisTokenService.saveRefreshToken(username, refreshToken);

        // 5. kafka 로그인 이벤트 발행
        loginEventProducer.sendLoginEvent(username,ipAddress,"LOGIN");

        return new LoginResponse(accessToken, refreshToken);
    }

    // 로그아웃
    public void logout(String username, String ipAddress, String accessToken) {

        // 1. Refresh Token 삭제
        redisTokenService.deleteRefreshToken(username);

        // 2. Access Token 블랙리스트 등록
        long remainingExpiration = jwtTokenProvider.getRemainingExpiration(accessToken);
        redisTokenService.addBlacklist(accessToken, remainingExpiration);

        // 3. Kafka 로그아웃 이벤트 발행
        loginEventProducer.sendLoginEvent(username, ipAddress, "LOGOUT");
    }


    // 토큰 재발급
    public LoginResponse reissue (String refreshToken) {


        // 1. Refresh Token 유효성 검증
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new IllegalArgumentException("유효하지않은 RefreshToken 정보입니다.");
        }

        // 2. username 정보 추출
        String username  = jwtTokenProvider.getUsername(refreshToken);

        // 3. Redis에 저장된 토큰과 비교
        String savedToken = redisTokenService.getRefreshToken(username);

        if(!refreshToken.equals(savedToken)) {
            throw new IllegalArgumentException("Refresh Token이 일치하지 않습니다.");
        }

        // 4. 새 Access Token 발급
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        String newAccessToken = jwtTokenProvider.createAccessToken(username,userDetails.getAuthorities());

        return new LoginResponse(newAccessToken, refreshToken);
    }

    // 탈퇴
    public void withdraw(String username) {

        AppUser appUser = appUserRepository.findByLoginId(username)
                .orElseThrow(()-> new IllegalArgumentException("존재하지 않는 유저입니다,"));

        // 2. Soft Delete 처리
        appUser.withdraw();
        appUserRepository.save(appUser);

        // 3. Redis Refresh Token 삭제
        redisTokenService.deleteRefreshToken(username);
    }

}