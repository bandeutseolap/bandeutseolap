package com.dobidan.bandeutseolap.domain.board.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

/**
 * AppBoardFileInfo
 *
 * 게시판 업로드 파일 정보 엔티티 클래스.
 *
 * - 데이터베이스 테이블(app_board_file_info)과 매핑됨
 * - @Entity: JPA가 이 클래스를 엔티티로 인식하도록 지정
 * - AppBoard와 N:1 관계 (여러 파일 → 하나의 게시글)
 *
 * - file_id                 : 파일 고유 식별자 (PK)
 *   - @Id: 기본 키 지정
 *   - @GeneratedValue: 기본 키 자동 생성 전략 (IDENTITY 방식)
 * - board_id                : 게시글 식별자 (FK → app_board.board_id)
 *   - @ManyToOne: AppBoard와 다대일 관계
 *   - FetchType.LAZY: 실제 사용 시점에 DB 조회 (성능 최적화)
 * - origin_file_name        : 사용자가 업로드한 원본 파일명
 * - saved_file_name         : 서버에 저장된 파일명 (UUID 변환)
 * - saved_file_path         : 파일 저장 경로
 * - file_extension          : 파일 확장자
 * - byte_size               : 파일 크기 (바이트 단위)
 * - large_file_yn           : 대용량 파일 여부
 * - saved_deadline          : 파일 삭제 예정일시 (nullable)
 * - physical_file_deleted_yn: 물리 파일 삭제 여부
 * - deleted_at              : 삭제일시 (nullable)
 */

@Entity
@Table(name = "app_board_file_info")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AppBoardFileInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "file_id")
    private Long fileId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    private AppBoard appBoard;

    @Column(name = "origin_file_name", nullable = false, length = 255)
    private String originFileName;

    @Column(name = "saved_file_name", nullable = false, length = 255)
    private String savedFileName;

    @Column(name = "saved_file_path", nullable = false, length = 255)
    private String savedFilePath;

    @Column(name = "file_extension", nullable = false, length = 20)
    private String fileExtension;

    @Column(name = "byte_size", nullable = false)
    private Long byteSize;

    @Column(name = "large_file_yn", nullable = false)
    private Boolean largeFileYn;

    @Column(name = "saved_deadline")
    private LocalDateTime savedDeadline;

    @Column(name = "physical_file_deleted_yn", nullable = false)
    private Boolean physicalFileDeletedYn;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Builder
    public AppBoardFileInfo(AppBoard appBoard, String originFileName, String savedFileName,
                            String savedFilePath, String fileExtension, Long byteSize,
                            Boolean largeFileYn) {
        this.appBoard             = appBoard;
        this.originFileName       = originFileName;
        this.savedFileName        = savedFileName;
        this.savedFilePath        = savedFilePath;
        this.fileExtension        = fileExtension;
        this.byteSize             = byteSize;
        this.largeFileYn          = largeFileYn;
        this.physicalFileDeletedYn = false;
    }
}
