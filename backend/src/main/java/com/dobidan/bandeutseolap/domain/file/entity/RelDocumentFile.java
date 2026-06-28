package com.dobidan.bandeutseolap.domain.file.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

/**
 * RelDocumentFile
 *
 * 문서(Document) - 파일(File) 연결 엔티티
 *
 * - 데이터베이스 테이블(rel_document_file)과 매핑됨
 * - 문서에 첨부된 파일 관계 정보를 관리한다.
 *
 * - document_file_rel_id : Document-File Rel PK
 * - document_id          : 문서 PK
 * - file_id              : 파일 PK
 * - file_role_cd         : 파일 역할 코드
 * - attached_by          : 첨부 사용자 ID
 * - attached_at          : 첨부 일시
 */

@Entity
@Table(name = "rel_document_file")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RelDocumentFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "document_file_rel_id")
    private Long documentFileRelId;

    @Column(name = "document_id", nullable = false)
    private Long documentId;

    @Column(name = "file_id", nullable = false)
    private Long fileId;

    @Column(name = "file_role_cd", nullable = false, length = 30)
    private String fileRoleCd;

    @Column(name = "attached_by", nullable = false)
    private Long attachedBy;

    @Column(name = "attached_at", nullable = false, updatable = false)
    private LocalDateTime attachedAt;

    @PrePersist
    public void prePersist() {
        this.attachedAt = LocalDateTime.now();
    }
}