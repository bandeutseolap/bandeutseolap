package com.dobidan.bandeutseolap.domain.file.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

/**
 * RelBoardFile
 *
 * 게시글(Board) - 파일(File) 연결 엔티티
 *
 * - 데이터베이스 테이블(rel_board_file)과 매핑됨
 * - 게시글에 첨부된 파일 관계 정보를 관리한다.
 *
 * - board_file_rel_id : Board-File Rel PK
 * - board_id          : 게시글 PK
 * - file_id           : 파일 PK
 * - file_role_cd      : 파일 역할 코드 (대표이미지, 첨부파일 등)
 * - attached_by       : 첨부 사용자 ID
 * - attached_at       : 첨부 일시
 */
@Entity
@Table(name = "rel_board_file")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RelBoardFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "board_file_rel_id")
    private Long boardFileRelId;

    @Column(name = "board_id", nullable = false)
    private Long boardId;

    @Column(name = "file_id", nullable = false)
    private Long fileId;

    @Column(name = "file_role_cd", nullable = false, length = 30)
    private String fileRoleCd;

    @Column(name = "attached_by", nullable = false)
    private Long attachedBy;

    @Column(name = "attached_at", nullable = false, updatable = false)
    private LocalDateTime attachedAt;

    @Builder
    public RelBoardFile(Long boardId, Long fileId, String fileRoleCd,
                        Long attachedBy, LocalDateTime attachedAt) {
        this.boardId    = boardId;
        this.fileId     = fileId;
        this.fileRoleCd = fileRoleCd;
        this.attachedBy = attachedBy;
        this.attachedAt = attachedAt;
    }

    @PrePersist
    public void prePersist() {
        this.attachedAt = LocalDateTime.now();
    }
}