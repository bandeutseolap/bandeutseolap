package com.dobidan.bandeutseolap.domain.project.controller;

import com.dobidan.bandeutseolap.domain.project.dto.request.TaskCreateRequest;
import com.dobidan.bandeutseolap.domain.project.dto.request.TaskUpdateRequest;
import com.dobidan.bandeutseolap.domain.project.dto.response.TaskResponse;
import com.dobidan.bandeutseolap.domain.project.service.AppTaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Tag(name = "Task", description = "태스크 관련 API")
@RestController
@RequestMapping("/projects/{projectId}/tasks")
@RequiredArgsConstructor
public class AppTaskController {

    private final AppTaskService appTaskService;

    @Operation(summary = "태스크 생성")
    @PostMapping
    public ResponseEntity<TaskResponse> createTask(
            @PathVariable Long projectId,
            @Valid @RequestBody TaskCreateRequest request) {
        return ResponseEntity.ok(appTaskService.createTask(projectId, request));
    }

    @Operation(summary = "태스크 목록 조회")
    @GetMapping
    public ResponseEntity<List<TaskResponse>> getTasks(@PathVariable Long projectId) {
        return ResponseEntity.ok(appTaskService.getTasks(projectId));
    }

    @Operation(summary = "태스크 상세 조회")
    @GetMapping("/{taskId}")
    public ResponseEntity<TaskResponse> getTask(
            @PathVariable Long projectId,
            @PathVariable Long taskId) {
        return ResponseEntity.ok(appTaskService.getTask(taskId));
    }

    @Operation(summary = "태스크 수정")
    @PutMapping("/{taskId}")
    public ResponseEntity<TaskResponse> updateTask(
            @PathVariable Long projectId,
            @PathVariable Long taskId,
            @Valid @RequestBody TaskUpdateRequest request) {
        return ResponseEntity.ok(appTaskService.updateTask(taskId, request));
    }
}
