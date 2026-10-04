package com.dobidan.bandeutseolap.domain.project.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class TaskCreateRequest {

    @NotNull
    private Long stepId;              // 단계 ID

    @NotBlank
    @Size(max = 255)
    private String taskName;          // 태스크명

    private LocalDate startDt;        // 시작일

    private LocalDate endDt;          // 종료일

    @NotNull
    private Long memberId;            // 담당 멤버 ID

    private Long priority;            // 우선순위

    @NotBlank
    @Size(max = 30)
    private String taskDifficultyCd;  // 난이도 코드

    @NotBlank
    @Size(max = 30)
    private String taskTypeCd;        // 태스크 유형 코드

    @NotBlank
    @Size(max = 30)
    private String taskStatusCd;      // 태스크 상태 코드

    private BigDecimal manMonth;      // 공수
}
