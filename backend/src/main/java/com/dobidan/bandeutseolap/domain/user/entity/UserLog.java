package com.dobidan.bandeutseolap.domain.user.entity;

/*
* UserLog 엔티티
*
* - 사용자 로그인 / 로그아웃 이력을 저장하는 테이블
* - kafka Consumer가 로그인 이벤트를 수신하면 해당 테이블에 저장
*
* */

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name="user_logs")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id",nullable = false)
    private Long userId;

    @Column(nullable = false)
    private String action;

    @Column(name = "ip_address")
    private String ipAddress;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

}
