package com.dobidan.bandeutseolap.domain.board.dto.response;

import com.dobidan.bandeutseolap.global.common.ResponseCode;
import com.dobidan.bandeutseolap.global.common.ResponseDto;
import com.dobidan.bandeutseolap.global.common.ResponseMessage;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Getter
public class PatchBoardResponseDto extends ResponseDto {


    public PatchBoardResponseDto() {
        super(ResponseCode.SUCCESS, ResponseMessage.SUCCESS);
    }


    // 성공
    public static ResponseEntity<PatchBoardResponseDto> success() {
        PatchBoardResponseDto patchBoardResponseDto = new PatchBoardResponseDto();
        return ResponseEntity.status(HttpStatus.OK).body(patchBoardResponseDto);
    }

    // 존재하지 않는 게시물
    public static ResponseEntity<ResponseDto> noExistBoard(){
        ResponseDto responseDto = new ResponseDto(ResponseCode.NOT_EXIST_BOARD, ResponseMessage.NOT_EXIST_BOARD);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseDto);
    }

    // 존재하지 않는 유저
    public static ResponseEntity<ResponseDto> noExistUser() {
        ResponseDto result = new ResponseDto(ResponseCode.NOT_EXIST_USER, ResponseMessage.NOT_EXIST_USER);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
    }

    // 수정 권한 있는 사용자
    public static ResponseEntity<ResponseDto> noPermission() {
        ResponseDto responseDto = new ResponseDto(ResponseCode.NO_PERMISSION, ResponseMessage.NO_PERMISSION);
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(responseDto);
    }
}
