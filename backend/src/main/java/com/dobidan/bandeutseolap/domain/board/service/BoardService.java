package com.dobidan.bandeutseolap.domain.board.service;

import com.dobidan.bandeutseolap.domain.board.dto.BoardDetailResponse;
import com.dobidan.bandeutseolap.domain.board.dto.BoardListResponse;
import com.dobidan.bandeutseolap.domain.board.dto.BoardRequest;
import com.dobidan.bandeutseolap.domain.board.dto.BoardResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * BoardService
 *
 * 게시판(Board) 관련 비즈니스 로직을 처리하기 위한 서비스 계층 인터페이스.
 *
 * - Controller와 Repository 사이에서 중간 역할 수행
 * - 트랜잭션 관리, 비즈니스 규칙 적용
 * - 추후 구현 클래스(BoardServiceImpl)에서 실제 로직 구현 예정
 *
 * 예시 메서드 (추후 추가 가능):
 * - 게시글 작성
 * - 게시글 목록 조회
 * - 게시글 상세 조회
 * - 게시글 수정
 * - 게시글 삭제
 */
public interface BoardService {
    // 게시글 작성
    BoardResponse createBoard(BoardRequest request);

    // 게시글 상세 조회
    BoardDetailResponse getBoard(Long boardId, Integer version);

    // 게시글 목록 조회
    Page<BoardListResponse> getBoardList(
            Long userId,
            String boardAreaCd,
            String keyword,
            String searchType,
            Pageable pageable
    );

    // 게시글 수정
    BoardResponse updateBoard(Long boardId, BoardRequest request);
}