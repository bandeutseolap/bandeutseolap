package com.dobidan.bandeutseolap.domain.board.repository;


import com.dobidan.bandeutseolap.domain.board.entity.AppBoard;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * BoardRepository
 *
 * 게시판 마스터 테이블 정보(AppBoard) 엔티티에 대한 데이터 접근 계층 (DAO) 역할을 담당하는 인터페이스.
 *
 * - JpaRepository<AppBoard, Long> 상속
 *   → 기본 CRUD 메서드 자동 제공
 *     ex) save(), findAll(), findById(), deleteById() 등
 *
 * - 추가로 필요한 쿼리는 메서드 이름 기반 쿼리 메서드 정의 가능
 *   ex) List<AppBoard> findByTitleContaining(String keyword);
 */

@Repository
public interface AppBoardRepository extends JpaRepository<AppBoard, Long> {

    // 비로그인 사용자 목록 조회
    @Query("""
    SELECT b FROM AppBoard b
    WHERE b.postStatusCd <> 'INACTIVE'
    AND b.visibleYn = true
    AND b.openTargetCd = 'ALL'
    AND (:boardAreaCd IS NULL OR b.boardAreaCd = :boardAreaCd)
    AND (
        :keyword IS NULL OR
        (:searchType = 'title' AND b.title LIKE CONCAT('%', :keyword, '%')) OR
        (:searchType = 'author' AND CONCAT('', b.writtenBy) LIKE CONCAT('%', :keyword, '%')) OR
        (:searchType = 'all' AND (
            b.title LIKE CONCAT('%', :keyword, '%')
            OR CONCAT('', b.writtenBy) LIKE CONCAT('%', :keyword, '%')
        ))
    )
    """)
    Page<AppBoard> findPublicBoards(
            @Param("boardAreaCd") String boardAreaCd,
            @Param("keyword") String keyword,
            @Param("searchType") String searchType,
            Pageable pageable
    );

    // 로그인 사용자 목록 조회
    @Query("""
    SELECT b FROM AppBoard b
    WHERE b.postStatusCd <> 'INACTIVE'
    AND (
        b.visibleYn = true
        OR b.writtenBy = :userId
    )
    AND (:boardAreaCd IS NULL OR b.boardAreaCd = :boardAreaCd)
    AND (
        :keyword IS NULL OR
        (:searchType = 'title' AND b.title LIKE CONCAT('%', :keyword, '%')) OR
        (:searchType = 'author' AND CONCAT('', b.writtenBy) LIKE CONCAT('%', :keyword, '%')) OR
        (:searchType = 'all' AND (
            b.title LIKE CONCAT('%', :keyword, '%')
            OR CONCAT('', b.writtenBy) LIKE CONCAT('%', :keyword, '%')
        ))
    )
    """)
    Page<AppBoard> findBoardsForLoginUser(
            @Param("userId") Long userId,
            @Param("boardAreaCd") String boardAreaCd,
            @Param("keyword") String keyword,
            @Param("searchType") String searchType,
            Pageable pageable
    );

}