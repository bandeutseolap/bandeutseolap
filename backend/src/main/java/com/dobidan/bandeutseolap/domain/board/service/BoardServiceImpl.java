package com.dobidan.bandeutseolap.domain.board.service;

import com.dobidan.bandeutseolap.domain.board.dto.BoardDetailResponse;
import com.dobidan.bandeutseolap.domain.board.dto.BoardListResponse;
import com.dobidan.bandeutseolap.domain.board.dto.BoardRequest;
import com.dobidan.bandeutseolap.domain.board.dto.BoardResponse;
import com.dobidan.bandeutseolap.domain.board.entity.AppBoard;
import com.dobidan.bandeutseolap.domain.board.entity.AppBoardContVer;
import com.dobidan.bandeutseolap.domain.board.repository.AppBoardContVerRepository;
import com.dobidan.bandeutseolap.domain.board.repository.AppBoardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

/**
 * BoardServiceImpl
 *
 * BoardService 인터페이스를 구현한 서비스 계층 클래스.
 *
 * - @Service: 스프링이 서비스 컴포넌트로 인식하도록 지정
 * - 실제 비즈니스 로직 구현 (게시글 작성, 조회, 수정, 삭제 등)
 * - Controller에서 호출되어 Repository와 상호작용하며 데이터 처리
 *
 * - @RequiredArgsConstructor: final로 선언된 필드를 인자로 받는 생성자를 자동 생성
 *   → 직접 생성자 코드를 작성하지 않아도 의존성 주입(DI)이 가능
 *   → Spring이 AppBoardRepository, AppBoardContVerRepository를
 *      자동으로 주입해줌 (생성자 주입 방식)
 *
 */
@Service
@RequiredArgsConstructor
public class BoardServiceImpl implements BoardService {
    private final AppBoardRepository appBoardRepository;
    private final AppBoardContVerRepository appBoardContVerRepository;

    // 게시글 생성
    @Override
    @Transactional
    public BoardResponse createBoard(BoardRequest request) {
        // 엔티티 생성 시 생성자 사용
        AppBoard board = AppBoard.builder()
                .title(request.title())
                .currentContentVersion(1)
                .boardAreaCd(request.boardAreaCd())
                .projectId(null)
                .visibleYn(request.visibleYn() != null && request.visibleYn())
                .openTargetCd(request.openTargetCd())
                .writtenBy(request.writtenBy())
                .bbsStatusCd("ACTIVE")
                .fixedTopYn(request.fixedTopYn() !=null && request.fixedTopYn())
                .noticeYn(request.noticeYn() !=null && request.noticeYn())
                .updatedBy(request.writtenBy())
                .build();


        AppBoard savedBoard = appBoardRepository.save(board);

        AppBoardContVer contentVer = AppBoardContVer.builder()
                .appBoard(savedBoard)
                .version(1)
                .content(request.content())
                .recoveredYn(false)
                .build();

        appBoardContVerRepository.save(contentVer);

        return new BoardResponse(
                savedBoard.getBoardId(),
                savedBoard.getTitle(),
                contentVer.getVersion(),
                contentVer.getContent()
        );
    }

    // 게시글 상세 조회
    @Override
    @Transactional(readOnly = true)
    public BoardDetailResponse getBoard(Long boardId, Integer version) {

        // 1. 게시글 조회
        AppBoard board = appBoardRepository.findById(boardId)
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));

        // 2. 버전 결정 (version 파라미터 없으면 최신 버전)
        int targetVersion = (version !=null) ? version : board.getCurrentContentVersion();

        // 3. 버전 내용 조회
        AppBoardContVer contVer = appBoardContVerRepository
                .findByAppBoard_BoardIdAndVersion(boardId, targetVersion)
                .orElseThrow(() -> new RuntimeException("해당 버전의 내용을 찾을 수 없습니다."));

        return new BoardDetailResponse(
                board.getBoardId(),
                board.getTitle(),
                board.getBoardAreaCd(),
                board.getOpenTargetCd(),
                board.getVisibleYn(),
                board.getFixedTopYn(),
                board.getNoticeYn(),
                board.getCurrentContentVersion(),
                board.getWrittenBy(),
                board.getWrittenAt(),
                board.getUpdatedAt(),
                board.getBbsStatusCd(),
                contVer.getContent()
        );
    }

    // 게시글 목록 조회
    @Override
    @Transactional(readOnly = true)
    public Page<BoardListResponse> getBoardList(Long userId,
                                                String boardAreaCd,
                                                String keyword,
                                                String searchType,
                                                Pageable pageable){
        Page<AppBoard> boards;

        if (userId == null) {
            // 비로그인 사용자
            boards = appBoardRepository.findPublicBoards(
                    boardAreaCd, keyword, searchType, pageable);
        } else {
            // 로그인 사용자
            boards = appBoardRepository.findBoardsForLoginUser(
                    userId, boardAreaCd, keyword, searchType, pageable);
        }

        return boards.map(board -> new BoardListResponse(
                board.getBoardId(),
                board.getTitle(),
                board.getBoardAreaCd(),
                board.getOpenTargetCd(),
                board.getVisibleYn(),
                board.getFixedTopYn(),
                board.getNoticeYn(),
                board.getCurrentContentVersion(),
                board.getWrittenBy(),
                board.getWrittenAt(),
                board.getUpdatedAt(),
                board.getBbsStatusCd()
        ));
    }

    // 게시글 수정
    @Override
    @Transactional
    public BoardResponse updateBoard(Long boardId, BoardRequest request){
     // 1. 게시글 조회
     AppBoard board  = appBoardRepository.findById(boardId)
             .orElseThrow(()-> new RuntimeException("게시글을 찾을 수 없습니다."));

     // 2. 작성자 본인 확인
     if (!board.getWrittenBy().equals(request.writtenBy())){
         throw new RuntimeException("수정 권한이 없습니다.");
     }

     // 3. app_board 테이블 업데이트
     board.update(request);
     appBoardRepository.save(board);

     // 4. app_board_cont_ver 새 버전 insert
     int nextVersion = board.getCurrentContentVersion();
     AppBoardContVer contVer = AppBoardContVer.builder()
             .appBoard(board)
             .version(nextVersion)
             .content(request.content())
             .recoveredYn(false)
             .build();
     appBoardContVerRepository.save(contVer);

     return new BoardResponse(
             board.getBoardId(),
             board.getTitle(),
             contVer.getVersion(),
             contVer.getContent()
     );

    }
}