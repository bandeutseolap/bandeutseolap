package com.dobidan.bandeutseolap.domain.project.repository;

import com.dobidan.bandeutseolap.domain.project.entity.AppProjectStep;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * AppProjectStepRepository
 *
 * app_project_step 테이블 접근을 담당하는 레포지토리.
 * 프로젝트별 단계 조회, 상태별 조회 등을 제공한다.
 */
public interface AppProjectStepRepository extends JpaRepository<AppProjectStep,Long> {
    // 프로젝트별 단계 조회
    List<AppProjectStep> findByProjectId(Long projectId);

    // 프로젝트별 상태로 조회
    List<AppProjectStep> findByProjectIdAndStepStatusCd(Long projectId, String stepStatusCd);
}

