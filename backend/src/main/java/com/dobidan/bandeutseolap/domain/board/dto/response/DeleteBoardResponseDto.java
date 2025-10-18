package com.dobidan.bandeutseolap.domain.board.dto.response;

import com.dobidan.bandeutseolap.global.common.ResponseCode;
import com.dobidan.bandeutseolap.global.common.ResponseDto;
import com.dobidan.bandeutseolap.global.common.ResponseMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Getter
public class DeleteBoardResponseDto extends ResponseDto{

    public DeleteBoardResponseDto() {
        super(ResponseCode.SUCCESS, ResponseMessage.SUCCESS);
    }

    public static ResponseEntity<DeleteBoardResponseDto> success() {
        return ResponseEntity.noContent().build();
        //DeleteBoardResponseDto deleteBoardResponseDto = new DeleteBoardResponseDto();
        //return ResponseEntity.status(HttpStatus.OK).body(deleteBoardResponseDto);
    }
    public static ResponseEntity<ResponseDto> noExistBoard() {
        ResponseDto deleteBoardResponseDto = new ResponseDto(ResponseCode.NOT_EXIST_BOARD, ResponseMessage.NOT_EXIST_BOARD);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(deleteBoardResponseDto);
    }

    public static ResponseEntity<ResponseDto> noExistUser() {
        ResponseDto deleteBoardResponseDto = new ResponseDto(ResponseCode.NOT_EXIST_USER, ResponseMessage.NOT_EXIST_USER);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(deleteBoardResponseDto);
    }

    public static ResponseEntity<ResponseDto> noPermission() {
        ResponseDto responseDto = new ResponseDto(ResponseCode.NO_PERMISSION, ResponseMessage.NO_PERMISSION);
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(responseDto);
    }
}
