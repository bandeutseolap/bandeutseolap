package com.dobidan.bandeutseolap.global.redis;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
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

    @Value("${jwt.access-expiration}")
    private long accessExpiration;

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Seoul"));

    // Refresh Token 저장 (Hash 방식)
    public void saveRefreshToken(String username, String refreshToken, Long userId, String ipAddress) {
        String key = "RT:" + username;

        Map<String, String> tokenData = new HashMap<>();
        tokenData.put("refreshToken", refreshToken);
        tokenData.put("userId", String.valueOf(userId));
        tokenData.put("issuedAt", now.format(formatter));
        tokenData.put("expiredAt", now.plus(Duration.ofMillis(refreshExpiration)).format(formatter));
        tokenData.put("lastAccessAt", now.format(formatter));
        tokenData.put("ipAddress", ipAddress);
        tokenData.put("status", "ACTIVE");

        redisTemplate.opsForHash().putAll(key, tokenData);
        redisTemplate.expire(key, refreshExpiration, TimeUnit.MILLISECONDS);
    }

    // Refresh Token 조회
    public String getRefreshToken(String username) {
        Object value = redisTemplate.opsForHash().get("RT:" + username, "refreshToken");
        return value != null ? value.toString() : null;
    }

    // Refresh Token 삭제
    public void deleteRefreshToken(String username) {
        redisTemplate.delete("RT:" + username);
    }

    // 마지막 접근 시간 업데이트
    public void updateLastAccessAt(String username) {
        redisTemplate.opsForHash().put(
                "RT:" + username,
                "lastAccessAt",
                LocalDateTime.now().toString()
        );
    }

    // 블랙리스트 등록
    public void addBlacklist(String accessToken, long expiration) {
        redisTemplate.opsForValue().set(
                "BL:" + accessToken,
                "logout",
                expiration,
                TimeUnit.MILLISECONDS
        );
    }

    public void blacklistAccessToken(String accessToken) {
        addBlacklist(accessToken, accessExpiration);
    }

    // 블랙리스트 확인
    public boolean isBlacklisted(String accessToken) {
        boolean result = Boolean.TRUE.equals(redisTemplate.hasKey("BL:" + accessToken));
        log.info("블랙리스트 확인 - token: {}, result: {}", accessToken, result);
        return result;
    }
}
