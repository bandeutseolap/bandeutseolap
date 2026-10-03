package com.dobidan.bandeutseolap.domain.project.dto.response;

import com.dobidan.bandeutseolap.domain.project.entity.AppProject;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * ProjectResponse
 *
 * 프로젝트 응답 DTO.
 * 프로젝트 목록 조회 시 사용한다.
 */
public record ProjectResponse(
        Long id,
        String projectCode,
        String projectName,
        String projectDesc,
        String projectStatusCd,
        String projectTypeCd,
        String visibilityCd,
        Long managerUserId,
        LocalDate plannedStartDt,
        LocalDate plannedEndDt,
        LocalDate actualStartDt,
        LocalDate actualEndDt,
        Boolean archiveYn,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    // Entity → DTO 변환 메서드
    public static ProjectResponse from(AppProject project) {
        return new ProjectResponse(
                project.getId(),
                project.getProjectCode(),
                project.getProjectName(),
                project.getProjectDesc(),
                project.getProjectStatusCd(),
                project.getProjectTypeCd(),
                project.getVisibilityCd(),
                project.getManagerUserId(),
                project.getPlannedStartDt(),
                project.getPlannedEndDt(),
                project.getActualStartDt(),
                project.getActualEndDt(),
                project.getArchiveYn(),
                project.getCreatedAt(),
                project.getUpdatedAt()
        );
    }
}
