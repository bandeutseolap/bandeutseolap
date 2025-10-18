package com.dobidan.bandeutseolap.kafka.consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;


@Service
public class KafkaConsumer {

    // 로거 이용해서 메시지 출력
    private final Logger LOGGER = LoggerFactory.getLogger(KafkaConsumer.class);

    @KafkaListener(topics = "BANDEUT_SEOLAP", groupId = "bandeut_seolap_GRP")
    public void cousume(String message){
        LOGGER.info(String.format("message received : %s",message));
    }

    //https://www.youtube.com/watch?v=YDZs5gwkutE&list=PLGRDMO4rOGcNLwoack4ZiTyewUcF6y6BU&index=13
    //11강
}
