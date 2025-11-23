package com.dobidan.bandeutseolap.global.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * SecurityConfig
 *
 * - Spring Security 설정을 담당하는 클래스
 * - JWT를 사용하기 때문에 세션/폼로그인/CSRF 등을 비활성화한다.
 * - 인증이 필요 없는 URL과 필요한 URL을 구분한다.
 * - JWT 필터(JwtAuthenticationFilter)를 Spring Security 필터 체인에 등록한다.
 */
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    /**
     * PasswordEncoder
     *
     * 비밀번호 암호화를 위해 BCrypt 사용.
     * 회원가입 시 비밀번호를 암호화하여 저장하고,
     * 로그인 시 암호화된 비밀번호와 비교한다.
     */

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * AuthenticationManager
     *
     * 로그인 시도 시 인증을 담당하는 AuthenticationManager를 Bean으로 등록.
     * Spring Security 내부 설정 사용.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    /**
     * SecurityFilterChain
     *
     * - CSRF, FormLogin, Session 비활성화
     * - 특정 URL은 인증 없이 접근 허용
     * - JWT 필터를 UsernamePasswordAuthenticationFilter 앞에 등록
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                //1) CSRF 비활성화 (JWT는 세션을 사용하지 않기 때문)
                .csrf(AbstractHttpConfigurer::disable)

                //2) 세션 사용 안 함
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                //3) 인증 허용/차단 URL 설정
                .authorizeHttpRequests(auth -> auth
                        // 회원가입, 로그인은 인증 없이 허용
                        .requestMatchers("/auth/**").permitAll()
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**").permitAll()  //Swagger 허용
                        // 그 외 모든 요청은 인증 필요
                        .anyRequest().authenticated()
                )

                //4) UsernamePasswordAuthenticationFilter 전에 JWT 필터 적용
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

}
