package com.dobidan.bandeutseolap.global.kafka;

import com.dobidan.bandeutseolap.domain.user.entity.AppUser;
import com.dobidan.bandeutseolap.domain.user.entity.UserLog;
import com.dobidan.bandeutseolap.domain.user.repository.AppUserRepository;
import com.dobidan.bandeutseolap.domain.user.repository.UserLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class LoginEventConsumer {

    private final UserLogRepository userLogRepository;
    private final AppUserRepository appUserRepository;

    /**
     * consumeLoginEvent()
     *
     * - "login-event" 토픽에서 메시지 수신
     * - 메시지 형식 : "username,ipAddress,action"
     * - 파싱 후 UserLog DB 저장
     */

    @KafkaListener(topics = "auth.login.event",groupId= "band-bandeutseolap-auth")
    public void consumeLoginEvent (String message){
        log.info("kaka 이벤트 수신 - message : {}",message);

        //1.메시지파싱
        String[] parts = message.split(",");
        String username = parts[0];
        String ipAddress = parts[1];
        String action = parts[2];

        //2.username으로 userId 조회
        Long userId = appUserRepository.findByLgnId(username)
                .map(user -> user.getId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));

        // 3. UserLog 저장
        UserLog userLog = UserLog.builder()
                .userId(userId)
                .action(action)
                .ipAddress(ipAddress)
                .createdAt(LocalDateTime.now())
                .build();

        userLogRepository.save(userLog);
        log.info("로그인 이력 저장 완료 - username: {}, action: {}", username, action);

    }


}