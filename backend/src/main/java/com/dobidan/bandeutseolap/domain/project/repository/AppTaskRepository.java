package com.dobidan.bandeutseolap.domain.project.repository;

import com.dobidan.bandeutseolap.domain.project.entity.AppTask;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AppTaskRepository extends JpaRepository<AppTask, Long> {

    // 프로젝트별 태스크 조회
    @Query("SELECT t FROM AppTask t WHERE t.project.id = :projectId")
    List<AppTask> findByProjectId(@Param("projectId") Long projectId);

    // 단계별 태스크 조회
    @Query("SELECT t FROM AppTask t WHERE t.step.id = :stepId")
    List<AppTask> findByStepId(@Param("stepId") Long stepId);

    // 멤버별 태스크 조회
    @Query("SELECT t FROM AppTask t WHERE t.member.id = :memberId")
    List<AppTask> findByMemberId(@Param("memberId") Long memberId);

    // 상태별 태스크 조회
    @Query("SELECT t FROM AppTask t WHERE t.project.id = :projectId and t.taskStatusCd = :taskStatusCd")
    List<AppTask> findByProject_IdAndTaskStatusCd(@Param("projectId") Long projectId, String taskStatusCd);
}
