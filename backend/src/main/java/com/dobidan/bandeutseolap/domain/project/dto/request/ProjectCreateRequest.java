package com.dobidan.bandeutseolap.domain.project.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

/**
 * ProjectCreateRequest
 *
 * 프로젝트 생성 요청 DTO.
 * 클라이언트에서 프로젝트 생성 시 필요한 데이터를 담는다.
 */
@Getter
@Setter
public class ProjectCreateRequest {

    @NotBlank
    @Size(max = 100)
    private String projectName;       // 프로젝트명

    private String projectDesc;       // 프로젝트 설명 (선택)

    @NotBlank
    @Size(max = 30)
    private String projectStatusCd;   // 프로젝트 상태 코드

    @NotBlank
    @Size(max = 30)
    private String projectTypeCd;     // 프로젝트 유형 코드

    @NotBlank
    @Size(max = 30)
    private String visibilityCd;      // 공개범위 코드

    @NotNull
    private Long managerUserId;       // 대표 담당자 ID

    @NotNull
    private LocalDate plannedStartDt; // 계획 시작일

    @NotNull
    private LocalDate plannedEndDt;   // 계획 종료일

}
