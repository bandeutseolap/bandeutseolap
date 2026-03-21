package com.dobidan.bandeutseolap.domain.board.repository;

import com.dobidan.bandeutseolap.domain.board.entity.Content;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * ContentRepository
 *
 * 콘텐츠(Content) 엔티티에 대한 데이터 접근 계층 (DAO) 역할을 담당하는 인터페이스.
 *
 * - JpaRepository<Content, Long> 상속
 *   → 기본 CRUD 메서드 자동 제공
 *     ex) save(), findAll(), findById(), deleteById() 등
 *
 * - Board와 N:1 관계이므로 board_id 기반 조회 메서드 제공
 *   ex) findByBoard_BoardId(Long boardId)
 *       → 특정 게시글에 속한 콘텐츠 목록 조회
 *
 */

@Repository
public interface ContentRepository extends JpaRepository<Content, Long> {

    // board_id로 content 목록 조회
    List<Content> findByBoard_BoardId(Long boardId);
}