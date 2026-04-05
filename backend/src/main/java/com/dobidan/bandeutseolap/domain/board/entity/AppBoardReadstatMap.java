package com.dobidan.bandeutseolap.domain.board.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

/**
 * AppBoardReadstatMap
 *
 * 게시판 게시글-사용자 읽음 여부 관리 엔티티 클래스.
 *
 * - 데이터베이스 테이블(app_board_readstat_map)과 매핑됨
 * - @Entity: JPA가 이 클래스를 엔티티로 인식하도록 지정
 * - AppBoard와 N:1 관계 (여러 사용자 읽음 기록 → 하나의 게시글)
 * - user_id + board_id 조합 유니크 제약조건
 *
 * - post_read_id  : 읽음 기록 고유 식별자 (PK)
 *   - @Id: 기본 키 지정
 *   - @GeneratedValue: 기본 키 자동 생성 전략 (IDENTITY 방식)
 * - user_id       : 사용자 식별자 (FK → app_user.user_id)
 * - board_id      : 게시글 식별자 (FK → app_board.board_id)
 *   - @ManyToOne: AppBoard와 다대일 관계
 *   - FetchType.LAZY: 실제 사용 시점에 DB 조회 (성능 최적화)
 * - first_read_at : 최초 읽은 일시 (자동 설정, 수정 불가)
 * - last_read_ver : 마지막으로 확인한 내용 버전
 * - last_read_at  : 마지막으로 읽은 일시 (자동 갱신)
 */

@Entity
@Table(name = "app_board_readstat_map")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AppBoardReadstatMap {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_read_id")
    private Long postReadId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    private AppBoard appBoard;

    @Column(name = "first_read_at", nullable = false, updatable = false)
    private LocalDateTime firstReadAt;

    @Column(name = "last_read_ver", nullable = false)
    private Integer lastReadVer;

    @Column(name = "last_read_at", nullable = false)
    private LocalDateTime lastReadAt;

    @Builder
    public AppBoardReadstatMap(Long userId, AppBoard appBoard, Integer lastReadVer) {
        this.userId      = userId;
        this.appBoard    = appBoard;
        this.lastReadVer = lastReadVer;
        this.firstReadAt = LocalDateTime.now();
        this.lastReadAt  = LocalDateTime.now();
    }
}