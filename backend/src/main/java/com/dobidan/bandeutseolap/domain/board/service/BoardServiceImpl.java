package com.dobidan.bandeutseolap.domain.board.service;

import com.dobidan.bandeutseolap.domain.board.dto.BoardRequest;
import com.dobidan.bandeutseolap.domain.board.dto.BoardResponse;
import com.dobidan.bandeutseolap.domain.board.entity.AppBoard;
import com.dobidan.bandeutseolap.domain.board.entity.AppBoardContVer;
import com.dobidan.bandeutseolap.domain.board.repository.AppBoardContVerRepository;
import com.dobidan.bandeutseolap.domain.board.repository.AppBoardRepository;
import jakarta.transaction.Transactional;
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
 * 현재는 뼈대만 정의되어 있으며,
 * 추후 BoardRepository를 주입받아 CRUD 기능을 구현할 예정.
 */
@Service
public class BoardServiceImpl implements BoardService {
    private final AppBoardRepository appBoardRepository;
    private final AppBoardContVerRepository appBoardContVerRepository;

    public BoardServiceImpl(AppBoardRepository appBoardRepository, AppBoardContVerRepository appBoardContVerRepository) {
        this.appBoardRepository = appBoardRepository;
        this.appBoardContVerRepository = appBoardContVerRepository;
    }

    //게시글 생성
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

}