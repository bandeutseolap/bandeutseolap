package com.dobidan.bandeutseolap.domain.project.dto.request;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * TaskUpdateRequest
 *
 * 태스크 수정 요청 DTO.
 * 수정은 바꾸고 싶은 것만 보내면 되어서 전부 선택값으로 구성했다.
 */
@Getter
@Setter
public class TaskUpdateRequest {

    @Size(max = 255)
    private String taskName;          // 태스크명

    private LocalDate startDt;        // 시작일

    private LocalDate endDt;          // 종료일

    private Long memberId;            // 담당 멤버 ID

    private Long priority;            // 우선순위

    @Size(max = 30)
    private String taskDifficultyCd;  // 난이도 코드

    @Size(max = 30)
    private String taskTypeCd;        // 태스크 유형 코드

    @Size(max = 30)
    private String taskStatusCd;      // 태스크 상태 코드

    private BigDecimal manMonth;      // 공수

    private Integer progress;         // 진행률
}