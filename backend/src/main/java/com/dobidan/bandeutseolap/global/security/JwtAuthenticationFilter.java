package com.dobidan.bandeutseolap.global.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JwtAuthenticationFilter
 *
 * - API 요청이 들어올 때마다 실행되는 필터.
 * - Authorization 헤더에서 Bearer 토큰을 꺼내 JWT 유효성을 검증한다.
 * - 유효한 토큰이면 로그인 인증 완료된 사용자 상태(SecurityContext)에 저장.
 * - 이후 컨트롤러에서는 인증된 사용자 정보(@AuthenticationPrincipal) 사용 가능.
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserDetailsService userDetailsService;

    /**
     * doFilterInternal()
     *
     * - 요청 헤더에서 Authorization 값을 가져옴
     * - "Bearer <token>" 형태인지 체크
     * - 토큰 유효성 검증
     * - username 꺼내서 DB 조회
     * - 인증 정보를 SecurityContext 에 저장
     */
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        String header = request.getHeader("Authorization");

        // Authorization 헤더 존재 + Bearer 형태인지 확인
        if (header != null && header.startsWith("Bearer ")) {

            String token = header.substring(7); // "Bearer " 뒤의 문자열만 추출

            // JWT 토큰 검증
            if (jwtTokenProvider.validateToken(token)) {
                String username = jwtTokenProvider.getUsername(token);

                // username 기반 유저 정보 조회
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                // 인증 객체 생성 (Spring Security 내부 인증 객체)
                UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );

                // SecurityContext에 저장 → 인증 완료 상태
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        }

        // 다음 필터 실행
        filterChain.doFilter(request, response);
    }
}
