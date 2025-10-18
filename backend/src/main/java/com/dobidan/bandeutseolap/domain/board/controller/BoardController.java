package com.dobidan.bandeutseolap.domain.board.controller;

import com.dobidan.bandeutseolap.domain.board.dto.request.DeleteBoardRequestDto;
import com.dobidan.bandeutseolap.domain.board.dto.request.PatchBoardRequestDto;
import com.dobidan.bandeutseolap.domain.board.dto.request.PostBoardRequestDto;
import com.dobidan.bandeutseolap.domain.board.dto.response.*;
import com.dobidan.bandeutseolap.domain.board.service.BoardService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;

    // 모든 게시물 불러오기
    @GetMapping("/board")
    public ResponseEntity<List<GetBoardResponseDto>> getBoard(){
        return boardService.getBoard();
    }

    // 게시물 작성하기
    @PostMapping("/board")
    public ResponseEntity<PostBoardResponseDto> postBoard(
            @RequestBody PostBoardRequestDto dto
//            @RequestParam Long userId
    ) {
        return boardService.postBoard(dto, dto.getUserId());
    }

    // 특정 게시물 가져오기
    @GetMapping("/board/{boardId}")
    public ResponseEntity<?> getDetailBoard(
//            @RequestParam id
            @PathVariable("boardId") Long boardId
    ){
        return boardService.getDetailBoard(boardId);
    }


    // 좋아요 등록하기 (PUT (44강 16:00~))
    @PutMapping("/board/{boardId}/favorite")
    public ResponseEntity<PutBoardResponseDto> putFavoriteBoard(
        @PathVariable("boardId") Long boardId,
        //@RequestBody String userId
        @RequestBody @Valid PostBoardRequestDto dto
    ){
        return boardService
                .putFavorite(boardId, dto.getUserId(), dto.getUserName());
    }
    
    // 좋아요 리스트 가져오기 (45강 첨부터 다시 듣기)
    @GetMapping("/board/{boardId}/favorite-list")
    //public ResponseEntity<List<GetFavoriteResponseDto>> getFavoriteList(
    public ResponseEntity<?> getFavoriteList(
            @PathVariable("boardId") Long boardId
    ){
        return boardService.getFavoriteList(boardId);
    }

    // 게시물 삭제하기 (56강) - 삭제된 이후에 반환할 본문이 없어서 204 뜸(정상인지 확인)
    @DeleteMapping("/board/{boardId}")
    public ResponseEntity<? super DeleteBoardResponseDto> deleteBoard(
            @PathVariable("boardId") Long boardId,
            @RequestBody DeleteBoardRequestDto dto
            ) {
        return boardService.deleteBoard(boardId, dto.getUserId());
    }

    // 게시물 수정하기 (61강 - )
    @PatchMapping("/board/{boardId}")
    public ResponseEntity<? super PatchBoardResponseDto> updateBoard(
            @PathVariable("boardId") Long boardId,
            //String userId,
            @RequestBody PatchBoardRequestDto patchBoardRequestDto
    ) {
        return boardService.updateBoard(patchBoardRequestDto, boardId, patchBoardRequestDto.getUserId());
    }

    // 최신 게시물 리스트 불러오기 64강
    // 주간 Top3 게시물 리스트 불러오기 65강
    // 인기 검색어 리스트 불러오기 66강
    // 패캠 듣기
}
