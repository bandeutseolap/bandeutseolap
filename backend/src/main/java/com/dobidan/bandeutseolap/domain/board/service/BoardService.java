package com.dobidan.bandeutseolap.domain.board.service; // 요즘 service => application 이라고 디렉토리 명 지정

import com.dobidan.bandeutseolap.domain.board.dto.request.PatchBoardRequestDto;
import com.dobidan.bandeutseolap.domain.board.dto.request.PostBoardRequestDto;
import com.dobidan.bandeutseolap.domain.board.dto.response.*;
import org.springframework.http.ResponseEntity;

import java.util.List;

// dto라는 곳에서 요청/응답 데이터를 담았으면, 서비스에서 dto를 반환해야하는 비즈니스 로직을 작성.
public interface BoardService {

    // 게시글 불러오기
    ResponseEntity<List<GetBoardResponseDto>> getBoard();

    // 특정 게시물 불러오기 (클라이언트 : id)
    ResponseEntity<?> getDetailBoard(Long boardId);

    // 게시물 작성
    ResponseEntity<PostBoardResponseDto> postBoard(PostBoardRequestDto dto, String userId);

    // 게시물 좋아요
    // 인증절차를 거쳐 어떤 유저가 좋아요 눌렀는지 확인 가능하게
    // 그래야 해당 게시물에 좋아요 누른 유저 리스트를 알 수 있음
    ResponseEntity<PutBoardResponseDto> putFavorite(Long boardId, String userId, String userName);

    ResponseEntity<?> getFavoriteList(Long boardId);

    ResponseEntity<? super DeleteBoardResponseDto> deleteBoard(Long boardId, String userId);

    ResponseEntity<? super PatchBoardResponseDto> updateBoard(PatchBoardRequestDto dto, Long boardId, String userId);
}
