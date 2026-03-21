package com.dobidan.bandeutseolap.domain.board.repository;

import com.dobidan.bandeutseolap.domain.board.entity.Content;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * ContentRepository
 *
 * 콘텐츠(Content) 엔티티에 대한 데이터 접근 계층 (DAO) 역할을 담당하는 인터페이스.
 *
 * - JpaRepository<Content, Long> 상속
 *   → 기본 CRUD 메서드 자동 제공
 *     ex) save(), findAll(), findById(), deleteById() 등
 * - 추후 필요 메서드 추가 예정
 *
 */

@Repository
public interface ContentRepository extends JpaRepository<Content, Long> {

}