package com.dobidan.bandeutseolap.domain.board.controller;

import com.dobidan.bandeutseolap.domain.board.dto.BoardDetailResponse;
import com.dobidan.bandeutseolap.domain.board.dto.BoardListResponse;
import com.dobidan.bandeutseolap.domain.board.dto.BoardRequest;
import com.dobidan.bandeutseolap.domain.board.dto.BoardResponse;
import com.dobidan.bandeutseolap.domain.board.service.BoardService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * BoardController
 *
 * 게시판(Board) 관련 요청을 처리하는 컨트롤러.
 *
 * - 클라이언트의 HTTP 요청을 받아 Service 계층으로 전달
 * - 응답 데이터를 가공하여 반환 (JSON 등)
 * - URL 매핑: /board
 */

@CrossOrigin(origins = "http://localhost:5173") // 필요한 도메인만!
@RestController
@RequestMapping("/board")
public class BoardController {
    private final BoardService boardService;

    // 생성자 주입
    public BoardController(BoardService boardService) {
        this.boardService = boardService;
    }

    // 게시글 작성 API
    @Operation(summary = "게시글 작성", description = "version=1")
    @PostMapping
    public ResponseEntity<BoardResponse> createBoard(@RequestBody BoardRequest request) {
        BoardResponse response = boardService.createBoard(request);
        return ResponseEntity.ok(response);
    }

    // 게시글 상세 조회 API
    @Operation(summary = "게시글 상세 조회", description = "board_id, version에 따른 게시글 상세 조회")
    @GetMapping("/{boardId}")
    public ResponseEntity<BoardDetailResponse> getBoard(@PathVariable Long boardId, @RequestParam(required = false) Integer version){
        BoardDetailResponse detailResponse = boardService.getBoard(boardId, version);
        return ResponseEntity.ok(detailResponse);
    }

    // 게시글 목록 조회 API
    @Operation(summary = "게시글 목록 조회", description = "게시글 목록 sort는 컬럼명 등 들어가야 함")
    @GetMapping
    public ResponseEntity<Page<BoardListResponse>> getBoardList(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) String boardAreaCd,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false, defaultValue = "all") String searchType,
            @PageableDefault(sort = "writtenAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(
                boardService.getBoardList(userId, boardAreaCd, keyword, searchType, pageable)
        );
    }



}