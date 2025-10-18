package com.dobidan.bandeutseolap.domain.board.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

//[ @NoArgsConstructor ]
/*
* Jackson 동작 순서
1. Jackson이 DTO 클래스를 찾음 (PostBoardRequestDto)
2. new PostBoardRequestDto() 기본 생성자로 객체를 생성
3. JSON의 키(title, content)와 DTO의 필드명을 매칭
4. setTitle("제목입니다"), setContent("내용입니다") 호출해서 값 주입
=> 즉, 기본 생성자 → setter 호출 순서로 객체가 완성됩니다.
*/

@Getter
@Setter
@NoArgsConstructor
// dto : 클라이언트(React, 앱 등) → 서버(Spring)로 들어오는 요청 데이터를 담는 그릇
// DTO 단에서 검증을 하면 컨트롤러 진입 시점에서 자동으로 검증 → 서비스/DB까지 잘못된 데이터가 가지 않도록 막을 수 있어요.
public class PostBoardRequestDto {

    // 컨트롤러 입장에서 화면한테 req하고 있음 : 화면에서 게시물 제목, 내용 데이터를 받아오고 있음
    //@NotBlank
    //(문자열 전용)
    //null, ""(빈 문자열), " "(공백만 있는 문자열) → 전부 검증 실패
    @NotBlank(message = "제목은 필수 입력값입니다.")
    String boardTitle;
    @NotBlank(message = "내용은 필수 입력값입니다.")
    String boardContent;
    @NotBlank
    String userId;
    @NotBlank
    String userName;
}
