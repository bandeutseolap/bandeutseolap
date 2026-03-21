package com.dobidan.bandeutseolap.domain.board.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Board
 *
 * 게시판(Board) 도메인의 엔티티 클래스.
 *
 * - 데이터베이스 테이블과 매핑됨
 * - @Entity: JPA가 이 클래스를 엔티티로 인식하도록 지정

 * - id: 게시글의 고유 식별자 (PK)
 *   - @Id: 기본 키 지정
 *   - @GeneratedValue: 기본 키 자동 생성 전략 (IDENTITY 방식)
 *
 * ※ 현재는 예시 필드(id)만 포함되어 있으며,
 *   추후 title, content, 작성자, 작성일 등 컬럼을 확장할 수 있음.
 */

@Entity
@Table(name="board")
@Getter
@NoArgsConstructor
public class Board {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "board_id")
    private Long id;

    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @Column(name = "is_public", nullable = false)
    private Boolean isPublic;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "author", nullable = false, length = 100)
    private String author;

    @Column(name = "status", nullable = false, length = 200)
    private String status;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "is_visible", nullable = false)
    private Boolean isVisible;

    @Column(name = "priority", nullable = false)
    private Integer priority;

    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL)
    private List<Content> contents = new ArrayList<>();

    // 추후 title, content 등의 필드를 추가 예정
}