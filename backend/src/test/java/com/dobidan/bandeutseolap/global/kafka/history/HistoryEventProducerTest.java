package com.dobidan.bandeutseolap.global.kafka.history;

import com.dobidan.bandeutseolap.global.kafka.history.HistoryEvent.EventType;
import com.dobidan.bandeutseolap.global.kafka.history.HistoryEvent.Method;
import java.time.Duration;
import java.util.UUID;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;

import java.time.LocalDateTime;
import java.util.Map;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("local")
@EmbeddedKafka(partitions = 3, topics = "dimple.history.event")
class HistoryEventProducerTest {

    @Autowired
    private HistoryEventProducer producer;

    @Autowired
    private EmbeddedKafkaBroker embeddedKafkaBroker;

    private Consumer<String, HistoryEvent> consumer;

    @BeforeEach
    void setUp() {
        // 테스트 전용 컨슈머 직접 구성 (JsonDeserializer로 HistoryEvent 역직렬화)
        Map<String, Object> consumerProps = KafkaTestUtils.consumerProps(
            "test-group", "true", embeddedKafkaBroker);
        consumerProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        consumerProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        consumerProps.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
        consumerProps.put(JsonDeserializer.VALUE_DEFAULT_TYPE, HistoryEvent.class.getName());

        consumer = new KafkaConsumer<>(consumerProps);
        embeddedKafkaBroker.consumeFromAnEmbeddedTopic(consumer, "dimple.history.event");
    }

    @AfterEach
    void tearDown() {
        consumer.close();
    }

    @Test
    void 같은_traceId면_같은_파티션에_들어간다() throws Exception {
        // given: MDC에 traceId를 명시적으로 고정 → 두 이벤트가 같은 traceId를 갖도록 보장
        String fixedTraceId = "test-trace-" + UUID.randomUUID();
        MDC.put("traceId", fixedTraceId);

        try{
            // REQUEST
            var header1 = producer.builderHeader("ORDER",
                HistoryEvent.MessageType.REQUEST, EventType.CLICK,
                "OrderController", "order-service", "/api/orders", Method.POST,
                new HistoryEvent.Actor("admin", "sess-1"),
                new HistoryEvent.Client("127.0.0.1",
                    new HistoryEvent.Client.UserAgent("User-Agent")),
                null);

            var event1 = new HistoryEvent(header1,
                new HistoryEvent.Body(Map.of("step", "request")));


            // RESPONSE
            var header2 = producer.builderHeader("ORDER",
                HistoryEvent.MessageType.RESPONSE, HistoryEvent.EventType.CLICK,
                "OrderController", "order-service", "/api/orders", HistoryEvent.Method.POST,
                new HistoryEvent.Actor("admin", "sess-1"),
                new HistoryEvent.Client("127.0.0.1", new HistoryEvent.Client.UserAgent("JUnit-Test")),
                new HistoryEvent.Result("0000", "처리 완료"));

            var event2 = new HistoryEvent(header2, new HistoryEvent.Body(Map.of("step", "response")));

            // 사전 검증: 실제로 같은 traceId인지 먼저 확인 (테스트 전제조건 명시화)
            assertThat(header1.traceId()).isEqualTo(header2.traceId());

            // when: 컨슈머로 직접 메시지 읽어 파티션 확인
            // (실제 검증 로직은 KafkaTestUtils.getRecords 등으로 구성)
            producer.publish(event1);
            producer.publish(event2);


            // then: poll로 직접 메시지 2개 읽어서 검증
            ConsumerRecords<String, HistoryEvent> records =
                KafkaTestUtils.getRecords(consumer, Duration.ofSeconds(10));

            assertThat(records.count()).isEqualTo(2); // ✅ 2개 모두 수신 확인

            // 핵심 검증 1: timestamp가 null이 아니고 직렬화/역직렬화가 깨지지 않았는지
            var partitions = new java.util.HashSet<Integer>();
            for (ConsumerRecord<String, HistoryEvent> record : records) {
                assertThat(record.key()).isEqualTo(fixedTraceId); // key가 traceId로 들어갔는지
                assertThat(record.value().header().timestamp()).isNotNull();
                partitions.add(record.partition());
            }

            // 핵심 검증: 같은 traceId(=같은 key)면 같은 파티션에 들어가야 함
            assertThat(partitions).hasSize(1); // ✅ 파티션이 1개로 모아져야 통과
            // 핵심 검증 2: key가 messageId가 아니라 traceId로 들어갔는지
    //        assertThat(record1.key()).isEqualTo(record1.value().header().traceId());

            // 핵심 검증 3: enum이 깨지지 않고 그대로 역직렬화 되는지
    //        assertThat(record1.value().header().eventType()).isEqualTo(HistoryEvent.EventType.CLICK);

        } finally {
            MDC.remove("traceId"); // ✅ 테스트 간 MDC 오염 방지 필수
        }
    }
}
