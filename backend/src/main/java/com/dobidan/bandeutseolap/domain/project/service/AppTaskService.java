package com.dobidan.bandeutseolap.domain.project.service;

import com.dobidan.bandeutseolap.domain.project.dto.request.TaskCreateRequest;
import com.dobidan.bandeutseolap.domain.project.dto.request.TaskUpdateRequest;
import com.dobidan.bandeutseolap.domain.project.dto.response.TaskResponse;
import com.dobidan.bandeutseolap.domain.project.entity.AppProject;
import com.dobidan.bandeutseolap.domain.project.entity.AppProjectMember;
import com.dobidan.bandeutseolap.domain.project.entity.AppProjectStep;
import com.dobidan.bandeutseolap.domain.project.entity.AppTask;
import com.dobidan.bandeutseolap.domain.project.repository.AppProjectMemberRepository;
import com.dobidan.bandeutseolap.domain.project.repository.AppProjectRepository;
import com.dobidan.bandeutseolap.domain.project.repository.AppProjectStepRepository;
import com.dobidan.bandeutseolap.domain.project.repository.AppTaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AppTaskService {

    private final AppTaskRepository appTaskRepository;
    private final AppProjectRepository appProjectRepository;
    private final AppProjectStepRepository appProjectStepRepository;
    private final AppProjectMemberRepository appProjectMemberRepository;

    // 태스크 생성
    @Transactional
    public TaskResponse createTask(Long projectId, TaskCreateRequest request) {

        AppProject project = appProjectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 프로젝트입니다."));

        AppProjectStep step = appProjectStepRepository.findById(request.getStepId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 단계입니다."));

        AppProjectMember member = appProjectMemberRepository.findById(request.getMemberId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 멤버입니다."));

        AppTask task = AppTask.builder()
                .project(project)
                .step(step)
                .taskName(request.getTaskName())
                .startDt(request.getStartDt())
                .endDt(request.getEndDt())
                .member(member)
                .priority(request.getPriority())
                .taskDifficultyCd(request.getTaskDifficultyCd())
                .taskTypeCd(request.getTaskTypeCd())
                .taskStatusCd(request.getTaskStatusCd())
                .manMonth(request.getManMonth())
                .build();

        return TaskResponse.from(appTaskRepository.save(task));
    }

    // 프로젝트별 태스크 목록 조회
    public List<TaskResponse> getTasks(Long projectId) {
        return appTaskRepository.findByProjectId(projectId)
                .stream()
                .map(TaskResponse::from)
                .collect(Collectors.toList());
    }

    // 태스크 상세 조회
    public TaskResponse getTask(Long taskId) {
        AppTask task = appTaskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 태스크입니다."));
        return TaskResponse.from(task);
    }

    // 태스크 수정
    @Transactional
    public TaskResponse updateTask(Long taskId, TaskUpdateRequest request) {
        AppTask task = appTaskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 태스크입니다."));

        if (request.getTaskName() != null) task.setTaskName(request.getTaskName());
        if (request.getStartDt() != null) task.setStartDt(request.getStartDt());
        if (request.getEndDt() != null) task.setEndDt(request.getEndDt());
        if (request.getPriority() != null) task.setPriority(request.getPriority());
        if (request.getTaskDifficultyCd() != null) task.setTaskDifficultyCd(request.getTaskDifficultyCd());
        if (request.getTaskTypeCd() != null) task.setTaskTypeCd(request.getTaskTypeCd());
        if (request.getTaskStatusCd() != null) task.setTaskStatusCd(request.getTaskStatusCd());
        if (request.getManMonth() != null) task.setManMonth(request.getManMonth());
        if (request.getProgress() != null) task.setProgress(request.getProgress());

        return TaskResponse.from(appTaskRepository.save(task));
    }
}
