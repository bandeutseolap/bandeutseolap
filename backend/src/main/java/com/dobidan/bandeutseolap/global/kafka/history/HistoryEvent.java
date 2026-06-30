package com.dobidan.bandeutseolap.global.kafka.history;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDateTime;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record HistoryEvent(
    Header header,
    Body body  // payload는 가변 구조라 Map으로 유연하게
) {
    public record Header(
        String messageId,
        String traceId, // TODO: trace UUID-Logger 연동
        String domain,
        MessageType messageType,   // REQUEST | RESPONSE | ERROR
        EventType eventType,     // CLICK | SCHEDULE | CHECK ...
        String sourceHandler,
        String producer,
        String url,
        Method method,
        LocalDateTime timestamp,     // ISO-8601 고정
        Actor actor,
        Client client,
        Result result
    ) {}

    public enum MessageType { REQUEST, RESPONSE, ERROR }
    public enum EventType { CLICK, SCHEDULE, CHECK }
    public enum Method { GET, POST, PUT, DELETE }

    public record Actor(String loginId, String sessionId) {}
    public record Client(String ip, UserAgent userAgent) {
        public record UserAgent(String raw) {}
    }
    public record Result(String code, String message) {}

    public record Body(Map<String, Object> payload) {}
}
