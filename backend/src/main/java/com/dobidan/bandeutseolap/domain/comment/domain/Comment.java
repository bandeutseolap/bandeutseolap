package com.dobidan.bandeutseolap.domain.comment.domain;


import jakarta.persistence.*;
import lombok.Getter;


@Getter
@Entity
public class Comment {
    // 댓글번호
    @Column(name = "COMMENT_ID")
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long commentId;
    // 게시글번호(board 테이블과 조인 관계)\
    //@ManyToOne(optional = false)
    @Column(name = "BOARD_ID")
    Long boardId;
    // 댓글 내용
    @Column(name = "COMMENT_CONT")
    String commentContent;
    // 댓글 작성자
    @Column(name = "CRAT_EMP")
    String commentWriter;

    // 댓글 작성일자
    @Column(name = "CRAT_DT")
    String commentCratDt;

    // ? 상위댓글번호
    @Column(name = "PREV_COMMENT_ID")
    Long prevCommentId;

    // 댓글 공개여부
    @Column(name = "OPEN_YN")
    Boolean commentOpenYn;
}
