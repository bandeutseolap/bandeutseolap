package com.dobidan.bandeutseolap.global.config;

import com.dobidan.bandeutseolap.global.kafka.history.HistoryEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

/**
 * application.yml의 공통 KafkaTemplate(StringSerializer)은 그대로 두고,
 * HistoryEvent 발행 전용으로만 JsonSerializer를 쓰는 ProducerFactory/KafkaTemplate을 별도 등록.
 *
 * KafkaProperties를 주입받아 bootstrap-servers 등 공통 설정(acks, retries 등)은
 * yml 값을 그대로 재사용하고, value-serializer만 프로그래매틱하게 override.
 */
@Configuration
public class KafkaConfig {
    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    // ✅ 기존 String,String 타입 유지
    @Bean
    public ProducerFactory<String, String> stringProducerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    @Primary  // ✅ String,String 타입 후보가 여러 개 생길 경우 기본값으로 지정 (LoginEventProducer는 @Qualifier 없이도 매칭됨)
    public KafkaTemplate<String, String> kafkaTemplate() {
        return new KafkaTemplate<>(stringProducerFactory());
    }

    // Producer
    @Bean
    public ProducerFactory<String, HistoryEvent> historyEventProducerFactory(
        KafkaProperties kafkaProperties,
        ObjectMapper objectMapper   // JacksonConfig에서 등록한 빈 재사용 (JavaTimeModule 적용된 것)
    ) {
        // yml의 spring.kafka.producer.* 설정(acks, retries, bootstrap-servers 등)을 그대로 가져옴
        Map<String, Object> configProps = new HashMap<>(kafkaProperties.buildProducerProperties(null));

        // value-serializer만 명시적으로 override (yml은 StringSerializer로 둔 채)
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

        DefaultKafkaProducerFactory<String, HistoryEvent> factory =
            new DefaultKafkaProducerFactory<>(configProps);

        // JsonSerializer에 JavaTimeModule 등록된 ObjectMapper 명시적으로 주입
        // (안 하면 별도 ObjectMapper가 생성되어 LocalDateTime 직렬화 오류 재발 가능)
        factory.setValueSerializer(new JsonSerializer<>(objectMapper));

        return factory;
    }

    @Bean
    public KafkaTemplate<String, HistoryEvent> historyEventKafkaTemplate(
        ProducerFactory<String, HistoryEvent> historyEventProducerFactory
    ) {
        return new KafkaTemplate<>(historyEventProducerFactory);
    }

    // Consumer
    @Bean
    public ConsumerFactory<String, HistoryEvent> historyEventConsumerFactory(
        KafkaProperties kafkaProperties,
        ObjectMapper objectMapper   // JacksonConfig의 JavaTimeModule 적용된 빈 재사용
    ) {
        // yml의 spring.kafka.consumer.* 공통 설정(group-id, auto-offset-reset 등)은 그대로 가져옴
        Map<String, Object> configProps = new HashMap<>(kafkaProperties.buildConsumerProperties(null));

        // 역직렬화 실패 시 컨슈머 스레드가 죽지 않도록 ErrorHandlingDeserializer로 감쌈 (권장)
        configProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
        configProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
        configProps.put(ErrorHandlingDeserializer.KEY_DESERIALIZER_CLASS, StringDeserializer.class);
        configProps.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, JsonDeserializer.class);

        // 타입 헤더(__TypeId__) 없이도 HistoryEvent로 바로 역직렬화하도록 고정
        configProps.put(JsonDeserializer.VALUE_DEFAULT_TYPE, HistoryEvent.class.getName());
        configProps.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
        configProps.put(JsonDeserializer.USE_TYPE_INFO_HEADERS, false);

        DefaultKafkaConsumerFactory<String, HistoryEvent> factory =
            new DefaultKafkaConsumerFactory<>(configProps);

        // ObjectMapper 명시적으로 주입 (LocalDateTime 직렬화 문제 재발 방지)
        factory.setValueDeserializer(
            new ErrorHandlingDeserializer<>(new JsonDeserializer<>(HistoryEvent.class, objectMapper))
        );

        return factory;
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, HistoryEvent> historyEventListenerContainerFactory(
        ConsumerFactory<String, HistoryEvent> historyEventConsumerFactory
    ) {
        ConcurrentKafkaListenerContainerFactory<String, HistoryEvent> factory =
            new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(historyEventConsumerFactory);
        return factory;
    }
}
