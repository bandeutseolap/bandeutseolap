package com.dobidan.bandeutseolap.global.redis;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * RedisTokenService
 *
 * - Redis를 사용하여 JWT Refresh Token을 관리하는 서비스 클래스.
 * - 로그인 시 Refresh Token을 Redis에 저장하고, 재발급 및 로그아웃 시 조회/삭제한다.
 * - key 형식 : "RT:{username}"
 * - 만료시간 : jwt.refresh-expiration 설정값 기준 (기본 14일)
 */

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisTokenService {

    private final StringRedisTemplate redisTemplate;

    @Value("${jwt.refresh-expiration}")
    private long refreshExpiration;

    //Refresh Token 저장 ()
    public void saveRefreshToken(String username, String refreshToken) {
        redisTemplate.opsForValue().set(
                "RT:" + username,
                refreshToken,
                refreshExpiration,
                TimeUnit.MILLISECONDS
        );
    }

    //Refresh Token 조회
    public String getRefreshToken (String username) {
        return redisTemplate.opsForValue().get("RT:" + username);
    }

    //Refresh Token 삭제
    public void deleteRefreshToken (String username) {
        redisTemplate.delete("RT:" + username);
    }

    public void addBlacklist(String accessToken, long expiration) {
        redisTemplate.opsForValue().set(
                "BL:" + accessToken,
                "logout",
                expiration,
                TimeUnit.MILLISECONDS
        );
    }

    @Value("${jwt.access-expiration}")
    private long accessExpiration;

    public void blacklistAccessToken(String accessToken) {
        addBlacklist(accessToken, accessExpiration);
    }

    public boolean isBlacklisted(String accessToken){
        boolean result = Boolean.TRUE.equals(redisTemplate.hasKey("BL:" + accessToken));
        log.info("블랙리스트 확인 - token: {}, result: {}", accessToken, result);
        return result;
    }

}
