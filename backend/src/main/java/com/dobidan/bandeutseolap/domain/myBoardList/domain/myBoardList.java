package com.dobidan.bandeutseolap.domain.myBoardList.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;

@Getter
@Entity
public class myBoardList {
    @Id
    Long boardId;
    String boardTitle;
    String boardContent;
    int commentCnt;
    int viewCnt;
    String boardWriter;
    String boardCratDt;
}
