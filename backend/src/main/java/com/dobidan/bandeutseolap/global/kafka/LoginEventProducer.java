package com.dobidan.bandeutseolap.global.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/*
* LoginEventProducer
*
* - 로그인 이벤트 발생 시, kafka 토픽으로 메시지를 발행하는 prodcer 클래스
* - 토픽명 : "login-event"
* - 메시지 형식 : "username,ipAddress,action"
* */
@Slf4j
@Service
@RequiredArgsConstructor
public class LoginEventProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    private static final String TOPIC = "auth.login.event";
    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public void sendLoginEvent(String username, String ipAddress, String action,
                               String userAgent, String requestUrl) {
        try {
            Map<String, String> event = new HashMap<>();
            event.put("loginId", username);
            event.put("ipAddress", ipAddress);
            event.put("action", action);
            event.put("timestamp", LocalDateTime.now(ZoneId.of("Asia/Seoul")).format(FORMATTER));
            event.put("userAgent", userAgent);
            event.put("requestUrl", requestUrl);

            String message = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(TOPIC, message);
            log.info("Kafka 이벤트 발행 - topic:{}, message:{}", TOPIC, message);
        } catch (Exception e) {
            log.error("Kafka 이벤트 발행 실패 - {}", e.getMessage());
        }
    }
}
