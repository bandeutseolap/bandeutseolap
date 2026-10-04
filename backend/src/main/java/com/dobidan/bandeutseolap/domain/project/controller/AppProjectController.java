package com.dobidan.bandeutseolap.domain.project.controller;

import com.dobidan.bandeutseolap.domain.project.dto.request.ProjectCreateRequest;
import com.dobidan.bandeutseolap.domain.project.dto.request.ProjectUpdateRequest;
import com.dobidan.bandeutseolap.domain.project.dto.response.ProjectResponse;
import com.dobidan.bandeutseolap.domain.project.service.AppProjectService;
import com.dobidan.bandeutseolap.domain.user.repository.AppUserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * AppProjectController
 *
 * 프로젝트 도메인 API 요청을 처리하는 컨트롤러.
 * 프로젝트 생성, 조회, 수정, 아카이브 기능을 제공한다.
 */
@Slf4j
@Tag(name = "Project", description = "프로젝트 관련 API")
@RestController
@RequestMapping("/projects")
@RequiredArgsConstructor
public class AppProjectController {

    private final AppProjectService appProjectService;
    private final AppUserRepository appUserRepository;

    @Operation(summary = "프로젝트 생성")
    @PostMapping
    public ResponseEntity<ProjectResponse> createProject(
            @Valid @RequestBody ProjectCreateRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

        Long userId = appUserRepository.findByLoginId(userDetails.getUsername())
                .map(user -> user.getId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));

        return ResponseEntity.ok(appProjectService.createProject(request, userId));
    }

    @Operation(summary = "프로젝트 목록 조회")
    @GetMapping
    public ResponseEntity<List<ProjectResponse>> getProjects() {
        return ResponseEntity.ok(appProjectService.getProjects());
    }

    @Operation(summary = "프로젝트 상세 조회")
    @GetMapping("/{projectId}")
    public ResponseEntity<ProjectResponse> getProject(@PathVariable Long projectId) {
        return ResponseEntity.ok(appProjectService.getProject(projectId));
    }

    @Operation(summary = "프로젝트 수정")
    @PutMapping("/{projectId}")
    public ResponseEntity<ProjectResponse> updateProject(
            @PathVariable Long projectId,
            @Valid @RequestBody ProjectUpdateRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

        Long userId = appUserRepository.findByLoginId(userDetails.getUsername())
                .map(user -> user.getId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));

        return ResponseEntity.ok(appProjectService.updateProject(projectId, request, userId));
    }

    @Operation(summary = "프로젝트 아카이브")
    @PatchMapping("/{projectId}/archive")
    public ResponseEntity<Void> archiveProject(
            @PathVariable Long projectId,
            @AuthenticationPrincipal UserDetails userDetails) {

        Long userId = appUserRepository.findByLoginId(userDetails.getUsername())
                .map(user -> user.getId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));

        appProjectService.archiveProject(projectId, userId);
        return ResponseEntity.ok().build();
    }
}