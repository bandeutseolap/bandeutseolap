package com.dobidan.bandeutseolap.domain.board.dto;
/**
 * BoardResponse
 *
 * 게시판(Board) 조회 응답을 담는 DTO.
 *
 * - id: 게시글 고유 식별자
 * - title: 게시글 제목
 * - content: 게시글 내용
 *
 * record를 사용하여 불변 객체로 정의되며,
 * Controller → 클라이언트로 응답 데이터를 전달할 때 사용.
 */
public record BoardResponse ( Long boardId,
                              String title,
                              Integer version,
                              String content) {
}
