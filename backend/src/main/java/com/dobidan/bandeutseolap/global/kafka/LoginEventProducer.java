package com.dobidan.bandeutseolap.global.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

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

    private final KafkaTemplate<String,String> kafkaTemplate;

    private static final String Topic = "auth.login.event";

    public void sendLoginEvent(String username,String ipAddress,String action){
        String message = username + "," + ipAddress + "," + action;
        kafkaTemplate.send(Topic,message);
        log.info("Kafka 이벤트 발행 - topic: {}, message: {}", Topic,message);
    }

}
