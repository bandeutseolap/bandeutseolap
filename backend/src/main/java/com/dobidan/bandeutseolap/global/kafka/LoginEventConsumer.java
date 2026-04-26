package com.dobidan.bandeutseolap.global.kafka;

import com.dobidan.bandeutseolap.domain.user.entity.AppUser;
import com.dobidan.bandeutseolap.domain.user.entity.UserLog;
import com.dobidan.bandeutseolap.domain.user.repository.AppUserRepository;
import com.dobidan.bandeutseolap.domain.user.repository.UserLogRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class LoginEventConsumer {

    private final UserLogRepository userLogRepository;
    private final AppUserRepository appUserRepository;
    private final ObjectMapper objectMapper;

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @KafkaListener(topics = "auth.login.event", groupId = "band-bandeutseolap-auth")
    public void consumeLoginEvent(String message) {
        log.info("Kafka 이벤트 수신 - message: {}", message);

        try {
            // 1. JSON 파싱
            Map<String, String> event = objectMapper.readValue(message, Map.class);
            String username   = event.get("loginId");
            String ipAddress  = event.get("ipAddress");
            String action     = event.get("action");
            String timestamp  = event.get("timestamp");
            String userAgent  = event.get("userAgent");
            String requestUrl = event.get("requestUrl");

            // 2. username으로 userId 조회
            Long userId = appUserRepository.findByLoginId(username)
                    .map(user -> user.getId())
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));

            // 3. UserLog 저장
            UserLog userLog = UserLog.builder()
                    .userId(userId)
                    .action(action)
                    .ipAddress(ipAddress)
                    .userAgent(userAgent)
                    .requestUrl(requestUrl)
                    .createdAt(LocalDateTime.parse(timestamp, FORMATTER))
                    .build();

            userLogRepository.save(userLog);
            log.info("로그인 이력 저장 완료 - username: {}, action: {}", username, action);

        } catch (Exception e) {
            log.error("Kafka 이벤트 처리 실패 - message: {}, error: {}", message, e.getMessage());
        }
    }
}