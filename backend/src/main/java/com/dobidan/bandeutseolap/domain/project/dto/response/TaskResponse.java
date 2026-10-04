package com.dobidan.bandeutseolap.domain.project.dto.response;

import com.dobidan.bandeutseolap.domain.project.entity.AppTask;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * TaskResponse
 *
 * 태스크 응답 DTO.
 * 태스크 조회 시 사용한다.
 */
public record TaskResponse(
        Long id,
        Long projectId,
        Long stepId,
        String taskName,
        LocalDate startDt,
        LocalDate endDt,
        Long memberId,
        Long priority,
        String taskDifficultyCd,
        String taskTypeCd,
        String taskStatusCd,
        BigDecimal manMonth,
        Integer progress
) {
    // Entity → DTO 변환 메서드
    public static TaskResponse from(AppTask task) {
        return new TaskResponse(
                task.getId(),
                task.getProject().getId(),
                task.getStep().getId(),
                task.getTaskName(),
                task.getStartDt(),
                task.getEndDt(),
                task.getMember().getId(),
                task.getPriority(),
                task.getTaskDifficultyCd(),
                task.getTaskTypeCd(),
                task.getTaskStatusCd(),
                task.getManMonth(),
                task.getProgress()
        );
    }
}