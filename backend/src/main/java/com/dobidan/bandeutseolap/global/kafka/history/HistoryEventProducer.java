package com.dobidan.bandeutseolap.global.kafka.history;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class HistoryEventProducer {
    private static final String TOPIC = "dimple.history.event";

    @Qualifier("historyEventKafkaTemplate")
    private final KafkaTemplate<String, HistoryEvent> kafkaTemplate;

    public HistoryEventProducer(
        @Qualifier("historyEventKafkaTemplate") KafkaTemplate<String, HistoryEvent> kafkaTemplate
    ) {
        this.kafkaTemplate = kafkaTemplate;
    }

    // Controller/AOP에서 호출하는 메인 발행 메서드
    public void publish(HistoryEvent event) {
        // TODO: 동일 요청 추적 시 동일 파티션 보장하고 싶으면 traceId, 아니면 partition key = messageId
        String key = event.header().traceId();

        CompletableFuture<SendResult<String, HistoryEvent>> future =
            kafkaTemplate.send(TOPIC, key, event);

        future.whenComplete((result, ex) -> {
            if (ex != null) {
                // 이력 데이터 유실은 치명적 → ERROR 레벨 + 재처리 큐/알림 연동 고려
                log.error("[" + TOPIC + "]" + " 전송 실패. traceId={}, messageId={}",
                    event.header().traceId(), key, ex);
            } else {
                log.debug("[" + TOPIC + "]" + "전송 성공. partition={}, offset={}",
                    result.getRecordMetadata().partition(),
                    result.getRecordMetadata().offset());
            }
        });
    }

    // 헤더 공통값 채우기
    public HistoryEvent.Header builderHeader(
        String domain,
        HistoryEvent.MessageType messageType,
        HistoryEvent.EventType eventType,
        String sourceHandler,
        String producer,
        String url,
        HistoryEvent.Method method,
        HistoryEvent.Actor actor,
        HistoryEvent.Client client,
        HistoryEvent.Result result
    ){
        String traceId = MDC.get("traceId") != null ?
            MDC.get("traceId") : UUID.randomUUID().toString();

        return new HistoryEvent.Header(
            UUID.randomUUID().toString(), // messageId
            traceId,
            domain,
            messageType,
            eventType,
            sourceHandler,
            producer,
            url,
            method,
            LocalDateTime.now(), // timestamp
            actor,
            client,
            result
        );
    }
}
