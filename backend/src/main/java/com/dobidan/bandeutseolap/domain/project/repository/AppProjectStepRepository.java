package com.dobidan.bandeutseolap.domain.project.repository;

import com.dobidan.bandeutseolap.domain.project.entity.AppProjectStep;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * AppProjectStepRepository
 *
 * app_project_step 테이블 접근을 담당하는 레포지토리.
 * 프로젝트별 단계 조회, 상태별 조회 등을 제공한다.
 */
public interface AppProjectStepRepository extends JpaRepository<AppProjectStep,Long> {
    // 프로젝트별 단계 조회
    @Query("SELECT s FROM AppProjectStep s WHERE s.project.id = :projectId")
    List<AppProjectStep> findByProjectId(@Param("projectId") Long projectId);

    // 프로젝트별 상태로 조회
    @Query("SELECT s FROM AppProjectStep s WHERE s.project.id = :projectId and s.stepStatusCd = :stepStatusCd")
    List<AppProjectStep> findByProjectIdAndStepStatusCd(@Param("projectId") Long projectId, @Param("stepStatusCd") String stepStatusCd);
}

