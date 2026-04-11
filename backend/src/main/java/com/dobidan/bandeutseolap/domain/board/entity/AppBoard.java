package com.dobidan.bandeutseolap.domain.board.entity;

import com.dobidan.bandeutseolap.domain.board.dto.BoardRequest;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

/**
 * AppBoard
 *
 * 게시판(Board) 도메인의 엔티티 클래스.
 * // 2026.03.29 테이블명 board > app_board
 *
 * - 데이터베이스 테이블(app_board)과 매핑됨
 * - @Entity: JPA가 이 클래스를 엔티티로 인식하도록 지정
 *
 * - board_id      : 게시글 고유 식별자 (PK)
 *   - @Id: 기본 키 지정
 *   - @GeneratedValue: 기본 키 자동 생성 전략 (IDENTITY 방식)
 * - title         : 게시글 제목
 * - current_content_version : 현재 내용 버전 (app_board_cont_ver 테이블과 연계)
 * - board_area_cd : 게시 영역 코드
 * - project_id    : 프로젝트 ID (nullable)
 * - visible_yn    : 노출 여부
 * - open_target_cd: 공개 대상 코드
 * - written_at    : 작성일시 (자동 설정)
 * - written_by    : 작성자 ID
 * - bbs_status_cd : 게시글 상태 코드
 * - fixed_top_yn  : 상단 고정 여부
 * - notice_yn     : 알림 여부
 * - noticed_at    : 알림일시 (nullable)
 * - updated_by    : 수정자 ID
 * - updated_at    : 수정일시 (자동 갱신)
 *
 */

@Entity
@Table(name = "app_board")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AppBoard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "board_id")
    private Long boardId;

    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @Column(name = "current_content_version", nullable = false)
    private Integer currentContentVersion;

    @Column(name = "board_area_cd", nullable = false, length = 30)
    private String boardAreaCd;

    @Column(name = "project_id")
    private Long projectId;

    @Column(name = "visible_yn", nullable = false)
    private Boolean visibleYn;

    @Column(name = "open_target_cd", nullable = false, length = 30)
    private String openTargetCd;

    @Column(name = "written_at", nullable = false, updatable = false)
    private LocalDateTime writtenAt;

    @Column(name = "written_by", nullable = false)
    private Long writtenBy;

    @Column(name = "bbs_status_cd", nullable = false, length = 30)
    private String bbsStatusCd;

    @Column(name = "fixed_top_yn", nullable = false)
    private Boolean fixedTopYn;

    @Column(name = "notice_yn", nullable = false)
    private Boolean noticeYn;

    @Column(name = "noticed_at")
    private LocalDateTime noticedAt;

    @Column(name = "updated_by", nullable = false)
    private Long updatedBy;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Builder
    public AppBoard(String title, Integer currentContentVersion, String boardAreaCd,
                    Long projectId, Boolean visibleYn, String openTargetCd,
                    Long writtenBy, String bbsStatusCd, Boolean fixedTopYn,
                    Boolean noticeYn, Long updatedBy) {
        this.title                  = title;
        this.currentContentVersion  = currentContentVersion;
        this.boardAreaCd            = boardAreaCd;
        this.projectId              = projectId;
        this.visibleYn              = visibleYn;
        this.openTargetCd           = openTargetCd;
        this.writtenBy              = writtenBy;
        this.bbsStatusCd            = bbsStatusCd;
        this.fixedTopYn             = fixedTopYn;
        this.noticeYn               = noticeYn;
        this.updatedBy              = updatedBy;
        this.writtenAt              = LocalDateTime.now();
        this.updatedAt              = LocalDateTime.now();
    }

    // update 로직을 위한 메서드 추가
    public void update(BoardRequest request, Long userId){
        this.title = request.title();
        this.openTargetCd = request.openTargetCd();
        this.visibleYn = request.visibleYn() != null && request.visibleYn();
        this.fixedTopYn = request.fixedTopYn() != null && request.fixedTopYn();
        this.noticeYn = request.noticeYn() != null && request.noticeYn();;
        this.updatedBy = userId;
        this.currentContentVersion = this.currentContentVersion + 1;
        this.updatedAt = LocalDateTime.now();
    }

    // delete 로직을 위한 메서드 추가
    public void delete(){
        this.bbsStatusCd = "INACTIVE";
        this.updatedAt   = LocalDateTime.now();
    }
}