package com.dobidan.bandeutseolap.domain.project.controller;

import com.dobidan.bandeutseolap.domain.project.entity.AppProjectMember;
import com.dobidan.bandeutseolap.domain.project.service.AppProjectMemberService;
import com.dobidan.bandeutseolap.domain.user.repository.AppUserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * AppProjectMemberController
 *
 * 프로젝트 멤버 도메인 API 요청을 처리하는 컨트롤러.
 * 멤버 초대, 조회, 탈퇴 기능을 제공한다.
 */
@Slf4j
@Tag(name = "Project Member", description = "프로젝트 멤버 관련 API")
@RestController
@RequestMapping("/projects/{projectId}/members")
@RequiredArgsConstructor
public class AppProjectMemberController {

    private final AppProjectMemberService appProjectMemberService;
    private final AppUserRepository appUserRepository;

    @Operation(summary = "멤버 초대")
    @PostMapping("/{userId}")
    public ResponseEntity<Void> inviteMember(
            @PathVariable Long projectId,
            @PathVariable Long userId,
            @AuthenticationPrincipal UserDetails userDetails) {

        Long invitedBy = appUserRepository.findByLoginId(userDetails.getUsername())
                .map(user -> user.getId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));

        appProjectMemberService.inviteMember(projectId, userId, invitedBy);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "멤버 목록 조회")
    @GetMapping
    public ResponseEntity<List<AppProjectMember>> getMembers(@PathVariable Long projectId) {
        return ResponseEntity.ok(appProjectMemberService.getMembers(projectId));
    }

    @Operation(summary = "멤버 탈퇴")
    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> leaveMember(
            @PathVariable Long projectId,
            @PathVariable Long userId) {
        appProjectMemberService.leaveMember(projectId, userId);
        return ResponseEntity.ok().build();
    }
}