package com.dobidan.bandeutseolap.domain.file.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

/**
 * AppFile
 *
 * 파일(File) 도메인의 엔티티 클래스.
 *
 * - 데이터베이스 테이블(app_file)과 매핑됨
 * - @Entity: JPA가 이 클래스를 엔티티로 인식하도록 지정
 *
 * - file_id             : 파일 고유 식별자 (PK)
 *   - @Id: 기본 키 지정
 *   - @GeneratedValue: 기본 키 자동 생성 전략 (IDENTITY 방식)
 * - storage_key         : 파일 저장소 키(경로)
 * - origin_file_name    : 원본 파일 이름
 * - stored_file_name    : 저장 파일 이름
 * - file_ext            : 파일 확장자
 * - mime_type           : MIME 유형
 * - file_size           : 파일 크기(byte)
 * - file_hash           : 파일 Hash값
 * - file_status_cd      : 파일 상태 코드
 * - delete_scheduled_at : 삭제 예정일시
 * - physical_deleted_yn : 물리 삭제 여부
 * - physical_deleted_at : 물리 삭제일시
 * - uploaded_by         : 업로드 사용자 ID
 * - uploaded_at         : 업로드일시
 *
 */

@Entity
@Table(name = "app_file")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AppFile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "file_id")
    private Long fileId;

    @Column(name = "storage_key", nullable = false, length = 255)
    private String storageKey;

    @Column(name = "origin_file_name", nullable = false, length = 255)
    private String originFileName;

    @Column(name = "stored_file_name", nullable = false, length = 255)
    private String storedFileName;

    @Column(name = "file_ext", nullable = false, length = 20)
    private String fileExt;

    @Column(name = "mime_type", nullable = false, length = 100)
    private String mimeType;

    @Column(name = "file_size", nullable = false)
    private Long fileSize;

    @Column(name = "file_hash", nullable = false, length = 128)
    private String fileHash;

    @Column(name = "file_status_cd", nullable = false, length = 30)
    private String fileStatusCd;

    @Column(name = "delete_scheduled_at")
    private LocalDateTime deleteScheduledAt;

    @Column(name = "physical_deleted_yn", nullable = false)
    private Boolean physicalDeletedYn = false;

    @Column(name = "physical_deleted_at")
    private LocalDateTime physicalDeletedAt;

    @Column(name = "uploaded_by", nullable = false)
    private Long uploadedBy;

    @Column(name = "uploaded_at")
    private LocalDateTime uploadedAt;

    @Builder
    public AppFile(String storageKey, String originFileName, String storedFileName,
                   String fileExt, String mimeType, Long fileSize, String fileHash,
                   String fileStatusCd, Long uploadedBy) {
        this.storageKey        = storageKey;
        this.originFileName    = originFileName;
        this.storedFileName    = storedFileName;
        this.fileExt           = fileExt;
        this.mimeType          = mimeType;
        this.fileSize          = fileSize;
        this.fileHash          = fileHash;
        this.fileStatusCd      = fileStatusCd;
        this.uploadedBy        = uploadedBy;
        this.physicalDeletedYn = false;
    }

    @PrePersist
    public void prePersist() {
        this.uploadedAt = LocalDateTime.now();
    }

    public void fileActivate() {
        this.fileStatusCd = "ACTIVE";
    }

    public void delete(){
        this.fileStatusCd = "DELETED";
        this.deleteScheduledAt = LocalDateTime.now().plusDays(90);
    }
}

