package com.dobidan.bandeutseolap.domain.board.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * AppBoardContVer
 *
 * 게시판 게시글 내용 버전 관리 엔티티 클래스.
 *
 * - 데이터베이스 테이블(app_board_cont_ver)과 매핑됨
 * - @Entity: JPA가 이 클래스를 엔티티로 인식하도록 지정
 * - AppBoard와 N:1 관계 (여러 버전 → 하나의 게시글)
 *
 * - content_id  : 콘텐츠 고유 식별자 (PK)
 *   - @Id: 기본 키 지정
 *   - @GeneratedValue: 기본 키 자동 생성 전략 (IDENTITY 방식)
 * - board_id    : 게시글 식별자 (FK → app_board.board_id)
 *   - @ManyToOne: AppBoard와 다대일 관계
 *   - FetchType.LAZY: 실제 사용 시점에 DB 조회 (성능 최적화)
 * - version     : 내용 버전 번호
 * - content     : 게시글 본문 (MEDIUMTEXT)
 * - recovered_version : 복원 기준 버전
 * - written_by : 작성 사용자 ID
 * - written_at : 작성 일시
 */

@Entity
@Table(name = "app_board_cont_ver")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AppBoardContVer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "content_id")
    private Long contentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    private AppBoard appBoard;

    @Column(name = "version", nullable = false)
    private Integer version;

    @Column(name = "content", nullable = false, columnDefinition = "MEDIUMTEXT")
    private String content;

    @Column(name = "recovered_version", nullable = false)
    private Integer recoveredVersion;

    @Column(name = "written_by", nullable = false)
    private Long writtenBy;

    @Column(name = "written_at", nullable = false, updatable = false)
    private LocalDateTime writtenAt;

    @Builder
    public AppBoardContVer(AppBoard appBoard, Integer version, String content,
                           Integer recoveredVersion, Long writtenBy) {
        this.appBoard         = appBoard;
        this.version          = version;
        this.content          = content;
        this.recoveredVersion = recoveredVersion;
        this.writtenBy        = writtenBy;
    }

    @PrePersist
    public void prePersist() {
        this.writtenAt = LocalDateTime.now();
    }
}