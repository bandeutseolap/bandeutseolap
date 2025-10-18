package com.dobidan.bandeutseolap.domain.board.dto.response;

import com.dobidan.bandeutseolap.domain.board.domain.Board;
import com.dobidan.bandeutseolap.domain.board.domain.Favorite;
import com.dobidan.bandeutseolap.global.common.ResponseCode;
import com.dobidan.bandeutseolap.global.common.ResponseDto;
import com.dobidan.bandeutseolap.global.common.ResponseMessage;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
//import org.springframework.kafka.core.KafkaTemplate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
//@JsonInclude(JsonInclude.Include.NON_NULL)
public class GetBoardResponseDto implements ResponseCode, ResponseMessage {

//    private String code;
//    private String message;

    private Long boardId;
    private String boardTitle;
    private String boardCont;
    private boolean openYn;
    private String boardWriter;
    private String boardCratDt;
    private int commentCount;
    private int viewCount;

    public GetBoardResponseDto(Board board) {


        this.boardId = board.getBoardId();
        this.boardTitle = board.getBoardTitle();
        this.boardCont = board.getBoardContent();
        this.openYn      = board.isOpenYn();
        this.boardWriter = board.getBoardWriter();
        this.boardCratDt = board.getBoardCratDt();
        this.commentCount  = board.getCommentCnt();
        this.viewCount     = board.getViewCnt();
    }

//    public GetBoardResponseDto(String code, String message) {
//        this.code = code;
//        this.message = message;
//
//    }

//    private final KafkaTemplate<String, String> kafkaTemplate;

    //전체 게시물 불러오기 성공
    public static ResponseEntity<List<GetBoardResponseDto>> success(List<Board> boards) {
        // 특정 게시물은 for문을 돌릴 필요 없지만, 모든 게시물을 가져오기 위해 리스트(배열)에 담겨서 반환
        List<GetBoardResponseDto> responseBody = new ArrayList<>();
        for(Board board : boards){
            GetBoardResponseDto result = new GetBoardResponseDto(board);
            responseBody.add(result);
//            kafkaTemplate.send("BANDEUT_SAMPLE", responseBody.toString());
        }


        /*
        if (key != null && !key.isEmpty()) {
            kafkaTemplate.send("_probe", key, msg);
        } else {
            kafkaTemplate.send("_probe", msg);
        }
        return ResponseEntity.ok("sent: " + msg);

         */
        return ResponseEntity.status(HttpStatus.OK).body(responseBody);
    }


    // 게시물 상세 불러오기
    public static ResponseEntity<GetBoardResponseDto> successDetail(Board board){
        GetBoardResponseDto responseBody = new GetBoardResponseDto(board);
        return ResponseEntity.status(HttpStatus.OK).body(responseBody);
    }


    // 실패 : 존재하지 않는 게시물
    public static ResponseEntity<ResponseDto> notExistBoard() {
        ResponseDto result = new ResponseDto(ResponseCode.NOT_EXIST_BOARD, ResponseMessage.NOT_EXIST_BOARD);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
    }



}
