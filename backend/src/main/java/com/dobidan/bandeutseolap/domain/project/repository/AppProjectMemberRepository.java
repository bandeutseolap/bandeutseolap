package com.dobidan.bandeutseolap.domain.project.repository;

import com.dobidan.bandeutseolap.domain.project.entity.AppProjectMember;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface AppProjectMemberRepository extends JpaRepository<AppProjectMember, Long> {

    // 프로젝트 멤버별 조회
    @Query("SELECT m FROM AppProjectMember m WHERE m.project.id = :projectId")
    List<AppProjectMember> findByProjectId(@Param("projectId") Long projectId);

    // 특정 유저가 프로젝트에 속해있는지 조회
    @Query("SELECT m FROM AppProjectMember m WHERE m.project.id = :projectId AND m.user.id = :userId")
    Optional<AppProjectMember> findByProjectIdAndUserId(@Param("projectId") Long projectId, @Param("userId") Long userId);

    // 프로젝트별 상태로 조회
    @Query("SELECT m FROM AppProjectMember m WHERE m.project.id = :projectId AND m.memberStatusCd = :statusCd")
    List<AppProjectMember> findByProjectIdAndMemberStatusCd(@Param("projectId") Long projectId, @Param("statusCd") String statusCd);
}
