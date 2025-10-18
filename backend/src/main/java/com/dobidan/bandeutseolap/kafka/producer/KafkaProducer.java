package com.dobidan.bandeutseolap.kafka.producer;

import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaProducer {
    // 메시지 : 로그로 생성한 메시지
    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaProducer.class);

    //KafkaTemplate 생성
    private final KafkaTemplate<String, String> kafkaTemplate;


    //전달될 로그를 Kafka topic으로 전송
    public void sendMessage(String message) {
        LOGGER.info(String.format("Message sent %s", message));
        kafkaTemplate.send("BANDEUT_SEOLAP", message);
    }
}
