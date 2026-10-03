package com.dobidan.bandeutseolap.domain.project.repository;

import com.dobidan.bandeutseolap.domain.project.entity.AppTask;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AppTaskRepository extends JpaRepository<AppTask, Long> {

    // 프로젝트별 태스크 조회
    List<AppTask> findByProjectId(Long projectId);

    // 단계별 태스크 조회
    List<AppTask> findByStepId(Long stepId);

    // 멤버별 태스크 조회
    List<AppTask> findByMemberId(Long memberId);

    // 상태별 태스크 조회
    List<AppTask> findByProjectIdAndTaskStatusCd(Long projectId, String taskStatusCd);
}
