package com.dobidan.bandeutseolap.domain.board.controller;

import com.dobidan.bandeutseolap.domain.board.dto.BoardDetailResponse;
import com.dobidan.bandeutseolap.domain.board.dto.BoardListResponse;
import com.dobidan.bandeutseolap.domain.board.dto.BoardRequest;
import com.dobidan.bandeutseolap.domain.board.dto.BoardResponse;
import com.dobidan.bandeutseolap.domain.board.service.BoardService;
import com.dobidan.bandeutseolap.domain.user.repository.AppUserRepository;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

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
@RequiredArgsConstructor
@RequestMapping("/board")
public class BoardController {
    private final BoardService boardService;
    private final AppUserRepository appUserRepository;

    // 게시글 작성 API
    @Operation(summary = "게시글 작성", description = "version=1")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<BoardResponse> createBoard(@RequestPart BoardRequest request,
                                                     @RequestPart(required = false) List<MultipartFile> files,
                                                     @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = getUserId(userDetails);
        BoardResponse response = boardService.createBoard(request, userId, files);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // 게시글 상세 조회 API
    @Operation(summary = "게시글 상세 조회", description = "board_id, version에 따른 게시글 상세 조회")
    @GetMapping("/{boardId}")
    public ResponseEntity<BoardDetailResponse> getBoard(@PathVariable Long boardId,
                                                        @RequestParam(required = false) Integer version){
        BoardDetailResponse detailResponse = boardService.getBoard(boardId, version);
        return ResponseEntity.ok(detailResponse);
    }

    // 게시글 목록 조회 API
    @Operation(summary = "게시글 목록 조회", description = "게시글 목록 sort는 컬럼명 등 들어가야 함")
    @GetMapping
    public ResponseEntity<Page<BoardListResponse>> getBoardList(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(required = false) String boardAreaCd,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false, defaultValue = "all") String searchType,
            @PageableDefault(sort = "writtenAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Long userId = getUserId(userDetails);
        return ResponseEntity.ok(
                boardService.getBoardList(userId, boardAreaCd, keyword, searchType, pageable)
        );
    }

    // 게시글 수정 API
    @Operation(summary = "게시글 수정", description = "작성자 본인의 글 수정")
    @PutMapping(value = "/{boardId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<BoardResponse> updateBoard(
            @PathVariable Long boardId,
            @RequestPart("request") BoardRequest request,
            @RequestPart(value = "files", required = false) List<MultipartFile> files,
            @AuthenticationPrincipal UserDetails userDetails
    ){
        Long userId = getUserId(userDetails);
        BoardResponse response = boardService.updateBoard(boardId, request, userId, files);
        return ResponseEntity.ok(response);
    }

    //게시글 삭제 API
    @Operation(summary = "게시글 삭제", description = "본인 게시글 id 삭제")
    @DeleteMapping("/{boardId}")
    public ResponseEntity<Map<String, String>> deleteBoard(@PathVariable Long boardId,
                                                           @AuthenticationPrincipal UserDetails userDetails){
        Long userId = getUserId(userDetails);
        boardService.deleteBoard(boardId, userId);
        return ResponseEntity.ok(Map.of("message", "게시글이 삭제되었습니다."));
    }

    // username → userId 추출 공통 메서드
    private Long getUserId(UserDetails userDetails) {
        return appUserRepository.findByLoginId(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."))
                .getId();
    }

}