package com.dobidan.bandeutseolap.domain.project.repository;

import com.dobidan.bandeutseolap.domain.project.entity.AppProjectMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AppProjectMemberRepository extends JpaRepository<AppProjectMember,Long> {

    // 프로젝트 멤버별 조회
    List<AppProjectMember> findByProjectId(Long projectId);

    // 특정유저가 프로젝트에 속해있는지 조회
    Optional<AppProjectMember> findByProjectIdAndUserId(Long projectId, Long userId);

    // 프로젝트별 상태로 조회
    List<AppProjectMember> findByProjectIdAndMemberStatusCd(Long projectId, String memberStatusCd);
}
