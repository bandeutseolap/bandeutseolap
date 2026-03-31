package com.dobidan.bandeutseolap.domain.board.repository;

import com.dobidan.bandeutseolap.domain.board.entity.AppBoardFileInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * AppBoardFileInfoRepository
 *
 * 첨부파일 정보 테이블 정보(AppBoardFileInfo) 엔티티에 대한 데이터 접근 계층 (DAO) 역할을 담당하는 인터페이스.
 *
 * - JpaRepository<AppBoardFileInfo, Long> 상속
 *   → 기본 CRUD 메서드 자동 제공
 *     ex) save(), findAll(), findById(), deleteById() 등
 *
 * - 추가로 필요한 쿼리는 메서드 이름 기반 쿼리 메서드 정의 가능
 *   ex) List<AppBoardFileInfo> findByTitleContaining(String keyword);
 */

@Repository
public interface AppBoardFileInfoRepository extends JpaRepository<AppBoardFileInfo, Long> {
}