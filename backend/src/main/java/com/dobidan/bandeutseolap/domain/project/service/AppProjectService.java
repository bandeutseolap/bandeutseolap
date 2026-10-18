package com.dobidan.bandeutseolap.domain.project.service;

import com.dobidan.bandeutseolap.domain.project.dto.request.ProjectCreateRequest;
import com.dobidan.bandeutseolap.domain.project.dto.request.ProjectUpdateRequest;
import com.dobidan.bandeutseolap.domain.project.dto.response.ProjectResponse;
import com.dobidan.bandeutseolap.domain.project.entity.AppProject;
import com.dobidan.bandeutseolap.domain.project.repository.AppProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * AppProjectService
 *
 * 프로젝트 도메인 비즈니스 로직을 담당하는 서비스.
 * 프로젝트 생성, 조회, 수정, 아카이브 기능을 제공한다.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AppProjectService {

    private final AppProjectRepository appProjectRepository;

    //프로젝트 생성
    @Transactional
    public ProjectResponse createProject(ProjectCreateRequest request, Long createdBy) {

    // UUID 앞 11자리 사용 (PRJ까지 총 14자리)
        String projectCode = "PRJ" + UUID.randomUUID().toString().replace("-", "").substring(0, 11).toUpperCase();

        AppProject project = AppProject.builder()
                .projectCode(projectCode)
                .projectName(request.getProjectName())
                .projectDesc(request.getProjectDesc())
                .projectStatusCd(request.getProjectStatusCd())
                .projectTypeCd(request.getProjectTypeCd())
                .visibilityCd(request.getVisibilityCd())
                .managerUserId(request.getManagerUserId())
                .plannedStartDt(request.getPlannedStartDt())
                .plannedEndDt(request.getPlannedEndDt())
                .createdBy(createdBy)
                .updatedBy(createdBy)
                .build();

        return ProjectResponse.from(appProjectRepository.save(project));

    }

    // 목록 조회 (파라미터 없음)
    public List<ProjectResponse> getProjects() {
        return appProjectRepository.findAll()
                .stream()
                .map(ProjectResponse::from)
                .collect(Collectors.toList());
    }

    // 상세 조회 (projectId 있음)
    public ProjectResponse getProject(Long projectId) {
        AppProject project = appProjectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 프로젝트입니다."));
        return ProjectResponse.from(project);
    }

    // 프로젝트 수정
    @Transactional
    public ProjectResponse updateProject(Long projectId, ProjectUpdateRequest request, Long updatedBy){
        AppProject project = appProjectRepository.findById(projectId).orElseThrow(() -> new IllegalArgumentException("존재하지않는 프로젝트입니다."));

        if (request.getProjectName() != null) project.setProjectName(request.getProjectName());
        if (request.getProjectDesc() != null) project.setProjectDesc(request.getProjectDesc());
        if (request.getProjectStatusCd() != null) project.setProjectStatusCd(request.getProjectStatusCd());
        if (request.getProjectTypeCd() != null) project.setProjectTypeCd(request.getProjectTypeCd());
        if (request.getVisibilityCd() != null) project.setVisibilityCd(request.getVisibilityCd());
        if (request.getManagerUserId() != null) project.setManagerUserId(request.getManagerUserId());
        if (request.getPlannedStartDt() != null) project.setPlannedStartDt(request.getPlannedStartDt());
        if (request.getPlannedEndDt() != null) project.setPlannedEndDt(request.getPlannedEndDt());
        if (request.getActualStartDt() != null) project.setActualStartDt(request.getActualStartDt());
        if (request.getActualEndDt() != null) project.setActualEndDt(request.getActualEndDt());
        project.setUpdatedBy(updatedBy);

        return ProjectResponse.from(appProjectRepository.save(project));
    }

    // 프로젝트 아카이브
    @Transactional
    public void archiveProject(Long projectId, Long updatedBy) {
        AppProject project = appProjectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 프로젝트입니다."));

        project.setArchiveYn(true);
        project.setArchivedAt(LocalDateTime.now());
        project.setUpdatedBy(updatedBy);
        appProjectRepository.save(project);
    }

}
