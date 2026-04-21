package com.dobidan.bandeutseolap.domain.file.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

/**
 * DmProjectFileUsage
 *
 * 프로젝트 파일 사용량 집계 테이블 엔티티
 *
 * - 데이터베이스 테이블(dm_project_file_usage)과 매핑됨
 * - 프로젝트 단위 파일 개수 및 용량 집계를 관리한다.
 * - 스케줄러 또는 이벤트 기반으로 갱신되는 파생 데이터이다.
 *
 * - project_id              : 프로젝트 PK (PK 역할)
 * - active_file_count       : 활성 파일 개수
 * - active_file_total_size  : 활성 파일 총 용량(Byte)
 * - updated_at              : 집계 갱신 일시
 */
@Entity
@Table(name = "dm_project_file_usage")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DmProjectFileUsage {

    @Id
    @Column(name = "project_id")
    private Long projectId;

    @Column(name = "active_file_count")
    private Integer activeFileCount;

    @Column(name = "active_file_total_size")
    private Long activeFileTotalSize;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    @PreUpdate
    public void prePersist() {
        this.updatedAt = LocalDateTime.now();
    }
}
