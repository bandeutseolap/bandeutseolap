package com.dobidan.bandeutseolap.domain.board.dto.response;

import com.dobidan.bandeutseolap.domain.board.domain.Favorite;
import com.dobidan.bandeutseolap.domain.board.dto.object.FavoriteListItem;
import com.dobidan.bandeutseolap.global.common.ResponseCode;
import com.dobidan.bandeutseolap.global.common.ResponseDto;
import com.dobidan.bandeutseolap.global.common.ResponseMessage;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

@Getter
public class GetFavoriteResponseDto implements ResponseCode, ResponseMessage {

//    private String code;
//    private String message;
    private List<FavoriteListItem> favoriteListItems;

    public GetFavoriteResponseDto(List<Favorite> favorites) {
        this.favoriteListItems = FavoriteListItem.favoriteList(favorites);
    }

//    public GetFavoriteResponseDto(String code, String message) {
//        this.code = code;
//        this.message = message;
//    }


    public static ResponseEntity<GetFavoriteResponseDto> success(List<Favorite> favorites) {
        GetFavoriteResponseDto getFavoriteResponseDto = new GetFavoriteResponseDto(favorites);
        return ResponseEntity.status(HttpStatus.OK).body(getFavoriteResponseDto);
    }


    // 존재하지 않는 게시물
    public static ResponseEntity<ResponseDto> notExistBoard(){
        ResponseDto responseDto = new ResponseDto(ResponseCode.NOT_EXIST_BOARD, ResponseMessage.NOT_EXIST_BOARD);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseDto);
    }
}
