package com.dobidan.bandeutseolap.domain.board.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    @GetMapping("/test")
    public String testBoardApi() {
        return "BE : Board API 해보자";
    }

}