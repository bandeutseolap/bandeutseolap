package com.dobidan.bandeutseolap.domain.board.domain;

import com.dobidan.bandeutseolap.domain.board.dto.request.PatchBoardRequestDto;
import com.dobidan.bandeutseolap.domain.board.dto.request.PostBoardRequestDto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
// 23강. 26:20~
public class Board {
    // 게시물 번호
    @Column(name = "BOARD_ID")
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long boardId;

    // 제목
    @Column(name = "BOARD_TTL")
    String boardTitle;
    // 내용
    @Column(name = "BOARD_CONT")
    String boardContent;
    // 공개여부
    @Column(name = "OPEN_YN")
    boolean openYn;
    // 작성자
    @Column(name = "CRAT_EMP")
    String boardWriter;
    // 작성일자
    @Column(name = "CRAT_DT")
    String boardCratDt;
    // 댓글 수
    @Column(name = "COMMENT_CNT")
    int commentCnt;
    // 조회 수
    @Column(name = "VIEW_CNT")
    int viewCnt;

    public Board(PostBoardRequestDto dto, String userId) {
        this.boardTitle = dto.getBoardTitle();
        this.boardContent = dto.getBoardContent();
        this.openYn = true;
        this.boardWriter = userId;

        //boardCratDt : 오늘 서버 시간으로 디폴트 지정
        LocalDateTime today = LocalDateTime.now();
        this.boardCratDt = String.valueOf(today);

        this.commentCnt = 0;
        this.viewCnt = 0;
    }

    public void increaseCommentCnt() {
        this.commentCnt++;
    }

    public void decreaseCommentCnt(){
        this.commentCnt--;
    }

    public void increaseViewCnt(){
        this.viewCnt++;
    }

    public void decreaseViewCnt(){
        this.viewCnt--;
    }

    public void patchBoard(PatchBoardRequestDto dto){
        this.boardContent = dto.getBoardContent();
        this.boardTitle = dto.getBoardTitle();

    }
}