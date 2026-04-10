package com.dobidan.bandeutseolap.domain.board.dto;

import java.time.LocalDateTime;

/**
 * BoardListResponse
 *
 * 게시판(Board) 목록 조회 응답을 담는 DTO.
 *
 * record를 사용하여 불변 객체로 정의되며,
 * 페이징 + 검색 + boardAreaCd 필터 결과를 반환할 때 사용.
 *
 */

public record BoardListResponse(Long boardId,
                                String title,
                                String boardAreaCd,
                                String openTargetCd,
                                Boolean visibleYn,
                                Boolean fixedTopYn,
                                Boolean noticeYn,
                                Integer currentContentVersion,
                                Long writtenBy,
                                LocalDateTime writtenAt,
                                LocalDateTime updateAt,
                                String bbsStatusCd) {
}
