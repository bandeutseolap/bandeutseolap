package com.dobidan.bandeutseolap.global.security;

import com.dobidan.bandeutseolap.global.redis.RedisTokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    private final RedisTokenService redisTokenService;


    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        String header = request.getHeader("Authorization");

        if (header != null && header.startsWith("Bearer ")) {

            String token = header.substring(7);

            // JWT 토큰 검증 (서명 + 만료시간 확인)
            if (jwtTokenProvider.validateToken(token)) {

                // 블랙리스트 확인 → 로그아웃된 토큰이면 즉시 차단
                if (redisTokenService.isBlacklisted(token)) {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.setCharacterEncoding("UTF-8");
                    response.getWriter().write("Logged out token.");
                    return;
                }

                String username = jwtTokenProvider.getUsername(token);
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );

                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        }

        // 다음 필터 실행 (if 블록 밖으로!)
        filterChain.doFilter(request, response);
    }
}