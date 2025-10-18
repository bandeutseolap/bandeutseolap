//package com.dobidan.bandeutseolap.kafka;
//
//import com.dobidan.bandeutseolap.kafka.producer.KafkaProducer;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.ResponseEntity;
//import org.springframework.kafka.core.KafkaTemplate;
//import org.springframework.web.bind.annotation.*;
//
//@RestController
//@RequiredArgsConstructor
////@RequestMapping("/kafka")
//public class KafkaSendController {
//
////    private final KafkaTemplate<String, String> kafkaTemplate;
//
//    // GET /kafka/send?msg=hello&key=k1  로 호출
////    @GetMapping("/board")
////    public ResponseEntity<String> send(@RequestParam String msg,
////                                       @RequestParam(required = false) String key) {
////        // key가 있으면 key 포함, 없으면 value만
////        if (key != null && !key.isEmpty()) {
////            kafkaTemplate.send("_probe", key, msg);
////        } else {
////            kafkaTemplate.send("_probe", msg);
////        }
////        return ResponseEntity.ok("sent: " + msg);
////    }
//
//
//    // 로그 메시지 가져오기
//    private final KafkaProducer kafkaProducer;
//
//    // ResponseEntity ok
//    @GetMapping("/publish")
//    public ResponseEntity<String> publish(@RequestParam("message") String message) {
//        kafkaProducer.sendMessage(message);
//        return ResponseEntity.ok("sent: " + message);
//    }
//
//}
//
