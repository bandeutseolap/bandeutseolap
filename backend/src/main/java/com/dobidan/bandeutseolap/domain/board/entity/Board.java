package com.dobidan.bandeutseolap.domain.board.entity;

import jakarta.persistence.*;

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
public class Board {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "board_id")
    private Long id;

    // 추후 title, content 등의 필드를 추가 예정
}