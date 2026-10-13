package com.dobidan.bandeutseolap.domain.project.service;

import com.dobidan.bandeutseolap.domain.project.dto.response.MemberResponse;
import com.dobidan.bandeutseolap.domain.project.entity.AppProject;
import com.dobidan.bandeutseolap.domain.project.entity.AppProjectMember;
import com.dobidan.bandeutseolap.domain.project.repository.AppProjectMemberRepository;
import com.dobidan.bandeutseolap.domain.project.repository.AppProjectRepository;
import com.dobidan.bandeutseolap.domain.user.entity.AppUser;
import com.dobidan.bandeutseolap.domain.user.repository.AppUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AppProjectMemberService {

    private final AppProjectMemberRepository appProjectMemberRepository;
    private final AppProjectRepository appProjectRepository;
    private final AppUserRepository appUserRepository;

    // 멤버 초대
    @Transactional
    public void inviteMember (Long projectId, Long userId, Long invitedBy){

        AppProject project = appProjectRepository.findById(projectId).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 프로젝트입니다."));
        AppUser user = appUserRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));
        AppUser inviter = appUserRepository.findById(invitedBy)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));

        appProjectMemberRepository.findByProjectIdAndUserId(projectId, userId)
                .ifPresent(m -> { throw new IllegalArgumentException("이미 프로젝트 멤버입니다."); });

        AppProjectMember member = AppProjectMember.builder()
                .project(project)
                .user(user)
                .memberStatusCd("ACTIVE")
                .invitedBy(inviter)
                .joinedAt(LocalDateTime.now())
                .updatedBy(invitedBy)
                .build();

        appProjectMemberRepository.save(member);

    }

    //프로젝트 멤버 조회
    public List<MemberResponse> getMembers(Long projectId) {
        return appProjectMemberRepository.findByProjectId(projectId)
                .stream()
                .map(MemberResponse::from)
                .collect(Collectors.toList());
    }

    //멤버 탈퇴
    @Transactional
    public void leaveMember(Long projectId, Long userId){
        AppProjectMember member = appProjectMemberRepository.findByProjectIdAndUserId(projectId,userId).orElseThrow(() -> new IllegalArgumentException("프로젝트 멤버가 아닙니다."));

        member.setMemberStatusCd("LEFT");
        member.setLeftAt(LocalDateTime.now());
        appProjectMemberRepository.save(member);
    }

}
