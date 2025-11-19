package com.dobidan.bandeutseolap.domain.board.dto;

/**
 * BoardRequest
 *
 * 게시판(Board) 작성/수정 요청을 담는 DTO.
 *
 * record는 불변 데이터 객체를 간단하게 정의하기 위한 기능으로,
 * title, content 필드를 생성자, getter, equals, hashCode, toString을 자동으로 생성해줌.
 *
 * 사용 예:
 * - 클라이언트가 게시글 작성 시 전달하는 요청 데이터(title, content)를 매핑
 * - Controller → Service 계층으로 데이터 전달 시 활용
 */
public record BoardRequest (String title, String content) {
}
