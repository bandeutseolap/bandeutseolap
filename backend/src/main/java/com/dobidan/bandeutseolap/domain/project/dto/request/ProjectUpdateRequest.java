package com.dobidan.bandeutseolap.domain.project.dto.request;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class ProjectUpdateRequest {

    @Size(max = 100)
    private String projectName;       // 프로젝트명

    private String projectDesc;       // 프로젝트 설명

    @Size(max = 30)
    private String projectStatusCd;   // 프로젝트 상태 코드

    @Size(max = 30)
    private String projectTypeCd;     // 프로젝트 유형 코드

    @Size(max = 30)
    private String visibilityCd;      // 공개범위 코드

    private Long managerUserId;       // 대표 담당자 ID

    private LocalDate plannedStartDt; // 계획 시작일

    private LocalDate plannedEndDt;   // 계획 종료일

    private LocalDate actualStartDt;  // 실제 시작일

    private LocalDate actualEndDt;    // 실제 종료일
}
