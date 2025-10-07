package com.dobidan.bandeutseolap.board.repository;

import com.dobidan.bandeutseolap.board.entity.Board;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * BoardRepository
 *
 * 게시판(Board) 엔티티에 대한 데이터 접근 계층 (DAO) 역할을 담당하는 인터페이스.
 *
 * - JpaRepository<Board, Long> 상속
 *   → 기본 CRUD 메서드 자동 제공
 *     ex) save(), findAll(), findById(), deleteById() 등
 *
 * - 추가로 필요한 쿼리는 메서드 이름 기반 쿼리 메서드 정의 가능
 *   ex) List<Board> findByTitleContaining(String keyword);
 */
public interface BoardRepository extends JpaRepository<Board, Long> {
}