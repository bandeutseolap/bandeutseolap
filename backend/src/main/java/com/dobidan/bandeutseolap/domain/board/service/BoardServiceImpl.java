package com.dobidan.bandeutseolap.domain.board.service;


import com.dobidan.bandeutseolap.domain.board.domain.Board;
import com.dobidan.bandeutseolap.domain.board.domain.Favorite;
import com.dobidan.bandeutseolap.domain.board.dto.request.PatchBoardRequestDto;
import com.dobidan.bandeutseolap.domain.board.dto.request.PostBoardRequestDto;
import com.dobidan.bandeutseolap.domain.board.dto.response.*;
import com.dobidan.bandeutseolap.domain.board.repository.BoardRepository;
import com.dobidan.bandeutseolap.domain.board.repository.FavoriteRepository;
import com.dobidan.bandeutseolap.domain.member.domain.Member;
import com.dobidan.bandeutseolap.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

// 38강 17:30~
// dto라는 곳에서 요청/응답 데이터를 담았으면, 서비스에서 dto를 반환해야하는 비즈니스 로직을 작성. => 서비스/컨트롤러 구조에 적합.
@Service
@RequiredArgsConstructor //래스에서 final 또는 @NonNull이 붙은 필드만 생성자 파라미터에 포함.
public class BoardServiceImpl implements BoardService{

    private final MemberRepository memberRepository;
    private final BoardRepository boardRepository;
    private final FavoriteRepository favoriteRepository;


    @Override
    public ResponseEntity<List<GetBoardResponseDto>> getBoard() {
        List<Board> result = boardRepository.findAll();

        return GetBoardResponseDto.success(result);

    }

    @Override
    public ResponseEntity<?> getDetailBoard(Long boardId) {
        Board result = boardRepository.selectBoardDetail(boardId);
        if(result == null) return GetBoardResponseDto.notExistBoard();

        return GetBoardResponseDto.successDetail(result); // 43강 24:00~
    }

    @Override
    public ResponseEntity<PostBoardResponseDto> postBoard(PostBoardRequestDto requestBody, String userId) {
        // 1. 먼저 클라이언트에서 받아온 userId가 존재하는 먼저 판단
        boolean existedUser = memberRepository.existsByUserId(userId);
        if (!existedUser) return PostBoardResponseDto.notExistUser();

        // 리턴 값이 string 이면 406 메시지 반환
//        ResponseEntity<PostBoardResponseDto> getCode = ResponseEntity.
//        System.out.println("statusValue = " + resp.getStatusCode().value()); // 406
//        System.out.println("statusEnum  = " + resp.getStatusCode());         // 406 NOT_ACCEPTABLE

        //if(getCode.getStatusCode().value() == 406) return PostBoardResponseDto.returnString();

        // 1-1. 존재하면, 엔티티를 생성해야 함. 레포지토리를 통해서 엔티티를 저장하면 됨
        Board board = new Board(requestBody, userId);
        boardRepository.save(board);
        // 1-2. (선택) 만약 존재하지 않으면, http 상태코드 메서드 반환(try ... catch 사용 권장)

        // 2. 뭔가의 로직에서 성공했으면 성공값 리턴
        return PostBoardResponseDto.success();
    }

    @Override
    public ResponseEntity<PutBoardResponseDto> putFavorite(Long boardId, String userId, String userName) {
        //존재하지 않는 유저라면, 메시지 출력
        boolean existedUser = memberRepository.existsByUserId(userId);
        if(!existedUser) return PutBoardResponseDto.notExistUser();

        //존재하지 않는 게시물이라면, 메시지 출력
        Board board = boardRepository.selectBoardDetail(boardId);
        if(board == null) return PutBoardResponseDto.notExistBoard();


        // 게시물 번호와 유저 id 찾기
        Favorite favorite = favoriteRepository.findByBoardIdAndUserId(boardId, userId);

        if(favorite == null){
            // 엔티티가 없으면 , 좋아요 저장, 좋아요 수 1 증가
            favorite = new Favorite(boardId, userId, userName);
            favoriteRepository.save(favorite);
            board.increaseCommentCnt();
        }else{
            // 존재하면 좋아요 지우기, 좋아요 수 1 감소
            favoriteRepository.delete(favorite);
            board.decreaseCommentCnt();
        }
        // 최종적으로 게시물에 좋아요 수 저장
        boardRepository.save(board);
        return PutBoardResponseDto.success();
    }

    @Override
    //public ResponseEntity<List<GetFavoriteResponseDto>> getFavoriteList(Long boardId) {
    public ResponseEntity<?> getFavoriteList(Long boardId) {
        // 존재하지 않는 게시물
        Board board = boardRepository.selectBoardDetail(boardId);
        if(board == null) return GetFavoriteResponseDto.notExistBoard();

        // 좋아요 리스트..
        List<Favorite> favorite = favoriteRepository.findFavoriteUsers(boardId);
        return GetFavoriteResponseDto.success(favorite);
    }

    @Override
    public ResponseEntity<? super DeleteBoardResponseDto> deleteBoard(Long boardId, String userId) {
        //존재하지 않는 게시물
        Board board = boardRepository.selectBoardDetail(boardId);
        if(board == null) return DeleteBoardResponseDto.noExistBoard();

        boolean existedUser = memberRepository.existsByUserId(userId);
        if(!existedUser) return DeleteBoardResponseDto.noExistUser();
        
        //(TODO) 삭제 권한이 있는 유저만 삭제 가능
        String writerUserId = board.getBoardWriter();
        boolean isWriter = writerUserId.equals(userId);
        if(!isWriter) return DeleteBoardResponseDto.noPermission();

        //(TODO) ex) 파일 첨부, 이미지, 댓글 등 게시물과 관련된 데이터도 모두 삭제해야함
        // 56강. 9:20~
        favoriteRepository.deleteByBoardId(boardId);

        boardRepository.delete(board); // 엔티티 제거
        return DeleteBoardResponseDto.success() ;
    }

    @Override
    public ResponseEntity<? super PatchBoardResponseDto> updateBoard(PatchBoardRequestDto dto, Long boardId, String userId) {
        //존재하지 않는 게시물
        Board board = boardRepository.selectBoardDetail(boardId);
        if(board == null) return PatchBoardResponseDto.noExistBoard();

        boolean existedUser = memberRepository.existsByUserId(userId);
        if(!existedUser) return PatchBoardResponseDto.noExistUser();

        String writerUserId = board.getBoardWriter();
        boolean isWriter = writerUserId.equals(userId);
        if(!isWriter) return DeleteBoardResponseDto.noPermission();


        // 삭제
//        favoriteRepository.deleteByBoardId(boardId);
//        boardRepository.delete(board);

        // 수정
        board.patchBoard(dto);
        boardRepository.save(board);


        return PatchBoardResponseDto.success();
    }


}
