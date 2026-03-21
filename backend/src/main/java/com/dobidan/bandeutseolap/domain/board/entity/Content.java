package com.dobidan.bandeutseolap.domain.board.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * Content
 *
 * 게시판(Board)에 종속된 콘텐츠 도메인의 엔티티 클래스.
 *
 * - 데이터베이스 테이블(content)과 매핑됨
 * - Board와 N:1 관계 (content 여러개 → board 1개)
 *
 * - @Entity: JPA가 이 클래스를 엔티티로 인식하도록 지정
 * - @Table: 매핑할 데이터베이스 테이블명 지정 (content)
 *
 * - seq      : 콘텐츠 고유 식별자 (PK)
 *   - @Id: 기본 키 지정
 *   - @GeneratedValue: 기본 키 자동 생성 전략 (IDENTITY 방식)
 *
 * - board    : 게시글 식별자 (FK → board.board_id)
 *   - @ManyToOne: Board와 다대일 관계
 *   - @JoinColumn: 외래키 컬럼명 지정 (board_id)
 *   - FetchType.LAZY: 실제 사용 시점에 DB 조회 (성능 최적화)
 *
 * - file_path  : 첨부파일 저장 경로
 * - body       : 게시글 본문 내용
 * - mime_type  : 첨부파일 형식 (예: image/png, application/pdf)
 * - created_at : 최초 생성일시
 * - updated_at : 최종 수정일시
 */

@Entity
@Table(name = "content")
@Getter
@NoArgsConstructor
public class Content {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "seq")
    private Long seq;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    private Board board;

    @Column(name = "file_path")
    private String filePath;

    @Column(name = "body")
    private String body;

    @Column(name = "mime_type", nullable = false, length = 100)
    private String mimeType;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}