package com.dobidan.bandeutseolap.domain.board.dto.response;

import com.dobidan.bandeutseolap.global.common.ResponseCode;
import com.dobidan.bandeutseolap.global.common.ResponseMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PutBoardResponseDto implements ResponseCode, ResponseMessage {
    String code;
    String message;

    // 성공
    public static ResponseEntity<PutBoardResponseDto> success(){
        PutBoardResponseDto putBoardResponseDto = new PutBoardResponseDto(ResponseCode.SUCCESS, ResponseMessage.SUCCESS);
        return ResponseEntity.status(HttpStatus.OK).body(putBoardResponseDto);
    }

    // 존재하지 않는 게시물
    public static ResponseEntity<PutBoardResponseDto> notExistBoard() {
        PutBoardResponseDto putBoardResponseDto = new PutBoardResponseDto(ResponseCode.NOT_EXIST_BOARD, ResponseMessage.NOT_EXIST_BOARD);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(putBoardResponseDto);
    }

    // 존재하지 않는 유저
    public static ResponseEntity<PutBoardResponseDto> notExistUser() {
        PutBoardResponseDto putBoardResponseDto = new PutBoardResponseDto(ResponseCode.NOT_EXIST_USER, ResponseMessage.NOT_EXIST_USER);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(putBoardResponseDto);
    }

}
