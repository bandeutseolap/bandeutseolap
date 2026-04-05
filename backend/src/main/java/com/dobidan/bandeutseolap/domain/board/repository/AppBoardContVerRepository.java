package com.dobidan.bandeutseolap.domain.board.repository;

import com.dobidan.bandeutseolap.domain.board.entity.AppBoardContVer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * AppBoardContVerRepository
 *
 * 게시글 내용 버전 관리 테이블 정보(AppBoardContVer) 엔티티에 대한 데이터 접근 계층 (DAO) 역할을 담당하는 인터페이스.
 *
 * - JpaRepository<AppBoardContVer, Long> 상속
 *   → 기본 CRUD 메서드 자동 제공
 *     ex) save(), findAll(), findById(), deleteById() 등
 *
 * - 추가로 필요한 쿼리는 메서드 이름 기반 쿼리 메서드 정의 가능
 *   ex) List<AppBoardContVer> findByTitleContaining(String keyword);
 */

@Repository
public interface AppBoardContVerRepository extends JpaRepository<AppBoardContVer, Long> {
}
