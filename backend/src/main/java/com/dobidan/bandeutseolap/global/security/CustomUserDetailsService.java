package com.dobidan.bandeutseolap.global.security;

import com.dobidan.bandeutseolap.domain.user.entity.User;
import com.dobidan.bandeutseolap.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import static org.springframework.security.core.userdetails.User.withUsername;

/**
 * CustomUserDetailsService
 *
 * - Spring Security가 인증을 수행할 때 호출되는 서비스
 * - username(로그인 아이디)을 기반으로 DB에서 실제 User를 조회한다.
 * - 조회된 User 엔티티를 Spring Security가 이해할 수 있는 UserDetails 형태로 변환해준다.
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService {

    /**
     * loadUserByUsername(String username)
     *
     * - 로그인 요청 시 실행되는 핵심 메서드
     * - username으로 DB에서 사용자 정보를 조회
     * - 찾은 정보를 Spring Security UserDetails 객체로 변환하여 반환
     * - 사용자 없으면 예외 발생 (Spring Security 규칙)
     */
    private final UserRepository userRepository;

    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException{

        // DB에서 사용자 조회
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다 : " + username));

        // UserDetails 형태로 변환하여 반환
        return withUsername(user.getUsername()) // username
                .password(user.getPassword())   // 암호화된 비밀번호
                .roles(user.getRole())          // 권한(Role)
                .build();

    }

}
