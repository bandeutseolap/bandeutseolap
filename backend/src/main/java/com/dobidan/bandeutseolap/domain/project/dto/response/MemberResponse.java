package com.dobidan.bandeutseolap.domain.project.dto.response;

import com.dobidan.bandeutseolap.domain.project.entity.AppProjectMember;

import java.time.LocalDateTime;

/**
 * MemberResponse
 *
 * 프로젝트 멤버 응답 DTO.
 */
public record MemberResponse(
        Long id,
        Long userId,
        Long projectId,
        String memberStatusCd,
        LocalDateTime joinedAt,
        LocalDateTime leftAt,
        Long updatedBy
) {
    public static MemberResponse from(AppProjectMember member) {
        return new MemberResponse(
                member.getId(),
                member.getUser().getId(),
                member.getProject().getId(),
                member.getMemberStatusCd(),
                member.getJoinedAt(),
                member.getLeftAt(),
                member.getUpdatedBy()
        );
    }
}