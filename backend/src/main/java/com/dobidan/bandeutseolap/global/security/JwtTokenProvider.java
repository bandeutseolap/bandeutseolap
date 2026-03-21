package com.dobidan.bandeutseolap.global.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

/**
 * JwtTokenProvider
 *
 * - JWT Access Token을 생성하고 검증하는 역할을 담당하는 클래스.
 * - username과 roles 정보를 넣어 JWT를 생성한다.
 * - 요청이 들어왔을 때 토큰이 유효한지 검사한다.
 */
@Component
public class JwtTokenProvider {


    /**
     * application.yml에서 가져올 secret key
     * jwt.secret: "아주 긴 랜덤 문자열"
     */

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.access-expiration}")
    private long accessExpiration;

    @Value("${jwt.refresh-expiration}")
    private long refreshExpiration;

    /**
     * secretKey를 기반으로 서명에 사용할 Key 객체 생성
     */
    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * createToken()
     *
     * - username + authorities(권한 리스트)를 기반으로 JWT 생성
     * - 토큰의 payload(claims)에 사용자 정보를 저장
     * - HS256 알고리즘으로 서명
     */
    public String createAccessToken(String username, Collection<? extends GrantedAuthority> roles) {
        Claims claims = Jwts.claims().setSubject(username);
        claims.put("roles", roles.stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList()));

        Date now = new Date();
        Date expire = new Date(now.getTime() + accessExpiration);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expire)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * createRefreshToken()
     *
     * - username 기반으로 Refresh Token 생성
     * - Access Token과 달리 username만 저장 (payload 최소화)
     * - 만료시간 : jwt.refresh-expiration 설정값 기준 (기본 14일)
     * - 생성된 토큰은 Redis에 저장하여 관리
     */

    public String createRefreshToken (String username) {
        Date now = new Date();
        return  Jwts.builder()
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + refreshExpiration))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * getUsername()
     *
     * - JWT 내부(payload)의 subject(username)을 가져오는 메서드
     */
    public String getUsername(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }


    /**
     * validateToken()
     *
     * - 서명 검증
     * - 만료 시간 검증
     * - 구조가 올바른지 검증
     * - 모두 통과하면 true, 아니면 false 반환
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token);
            return true;     // 문제 없으면 유효한 토큰
        } catch (JwtException | IllegalArgumentException e) {
            return false;    // 서명 오류, 만료 등 문제 발생
        }
    }
}


