package com.dobidan.bandeutseolap.domain.user.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * User 엔티티
 *
 * - Spring Security + JWT 기반 인증에서 핵심이 되는 사용자 정보 테이블
 * - username(로그인 아이디), password(암호), role(권한) 필드를 가짐
 */

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;   // MariaDB의 AUTO_INCREMENT 방식으로 PK 증가

    @Column(nullable = false, unique = true)  // AUTO_INCREMENT 방식으로 PK 증가
    private String username;    // 로그인 아이디 (이메일도 가능)

    @Column(nullable = false)
    private String password;    // 암호화된 비밀번호 저장 (평문X)

    @Column(nullable = false)
    private String role;      // 권한 (예: "USER", "ADMIN")
}
