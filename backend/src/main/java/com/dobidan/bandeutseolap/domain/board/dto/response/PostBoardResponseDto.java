package com.dobidan.bandeutseolap.domain.board.dto.response;


import com.dobidan.bandeutseolap.domain.board.dto.request.PostBoardRequestDto;
import com.dobidan.bandeutseolap.global.common.ResponseCode;
import com.dobidan.bandeutseolap.global.common.ResponseMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

// 38강 4:55~
/*
Spring Boot가 JSON으로 응답을 내려주는 과정은 HttpMessageConverter →
보통 MappingJackson2HttpMessageConverter (Jackson 라이브러리)입니다.

Jackson은 Java 객체 → JSON 직렬화를 할 때 “필드 값”을 꺼내야 하는데,
기본적으로는 public getter 메서드를 통해 값을 읽어요.

만약 DTO에 getter가 하나도 없으면, Jackson이 값을 읽을 방법이 없어서 {}(빈 JSON)으로 응답하거나 직렬화 에러가 날 수 있어요.
*/
@Getter
@NoArgsConstructor
@AllArgsConstructor // final이든, non-final이든, 심지어 private이든 전부 포함. => 불필요한 생성자가 생길 수 있음.
public class PostBoardResponseDto implements ResponseCode, ResponseMessage {
    String code;
    String message;


//    private PostBoardResponseDto() {
//    }


    // 성공
    public static ResponseEntity<PostBoardResponseDto> success() {
        PostBoardResponseDto result = new PostBoardResponseDto(ResponseCode.SUCCESS, ResponseMessage.SUCCESS);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }


    // 실패 : 존재하지 않는 유저
    public static ResponseEntity<PostBoardResponseDto> notExistUser() {
        PostBoardResponseDto result = new PostBoardResponseDto(ResponseCode.NOT_EXIST_USER, ResponseMessage.NOT_EXIST_USER);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
    }

    //컨트롤러가 JSON이 아니라 뷰/문자열을 반환
    public static ResponseEntity<PostBoardResponseDto> returnString() {
        PostBoardResponseDto result = new PostBoardResponseDto(ResponseCode.NOT_ACCEPTABLE, ResponseMessage.NOT_ACCEPTABLE);
        return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(result);
    }


    // 유효성 검사

    // db 에러

}