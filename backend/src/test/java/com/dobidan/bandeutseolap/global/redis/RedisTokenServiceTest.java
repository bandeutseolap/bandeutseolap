package com.dobidan.bandeutseolap.global.redis;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RedisTokenServiceTest {

    @Mock
    private StringRedisTemplate redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @InjectMocks
    private RedisTokenService redisTokenService;

    @Test
    @DisplayName("Refresh Token 저장")
    void saveRefreshToken() {
        // given
        String username = "testuser";
        String refreshToken = "refresh-token-value";
        ReflectionTestUtils.setField(redisTokenService, "refreshExpiration", 1209600000L);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        // when
        redisTokenService.saveRefreshToken(username, refreshToken);

        // then
        verify(valueOperations).set("RT:testuser", refreshToken, 1209600000L, TimeUnit.MILLISECONDS);
    }

    @Test
    @DisplayName("Refresh Token 조회")
    void getRefreshToken() {
        // given
        String username = "testuser";
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get("RT:testuser")).thenReturn("refresh-token-value");

        // when
        String result = redisTokenService.getRefreshToken(username);

        // then
        assertThat(result).isEqualTo("refresh-token-value");
    }

    @Test
    @DisplayName("Refresh Token 삭제")
    void deleteRefreshToken() {
        // given
        String username = "testuser";

        // when
        redisTokenService.deleteRefreshToken(username);

        // then
        verify(redisTemplate).delete("RT:testuser");
    }

    @Test
    @DisplayName("블랙리스트 등록")
    void addBlacklist() {
        // given
        String accessToken = "access-token-value";
        long expiration = 3600000L;
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        // when
        redisTokenService.addBlacklist(accessToken, expiration);

        // then
        verify(valueOperations).set("BL:" + accessToken, "logout", expiration, TimeUnit.MILLISECONDS);
    }

    @Test
    @DisplayName("블랙리스트 확인 - 등록된 토큰")
    void isBlacklisted_true() {
        // given
        String accessToken = "access-token-value";
        when(redisTemplate.hasKey("BL:" + accessToken)).thenReturn(true);

        // when
        boolean result = redisTokenService.isBlacklisted(accessToken);

        // then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("블랙리스트 확인 - 미등록 토큰")
    void isBlacklisted_false() {
        // given
        String accessToken = "access-token-value";
        when(redisTemplate.hasKey("BL:" + accessToken)).thenReturn(false);

        // when
        boolean result = redisTokenService.isBlacklisted(accessToken);

        // then
        assertThat(result).isFalse();
    }
}