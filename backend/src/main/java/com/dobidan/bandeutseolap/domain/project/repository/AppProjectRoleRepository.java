package com.dobidan.bandeutseolap.domain.project.repository;

import com.dobidan.bandeutseolap.domain.project.entity.AppProjectRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AppProjectRoleRepository extends JpaRepository<AppProjectRole,Long> {

    // 프로젝트별 역할 조회
    List<AppProjectRole> findByProjectId(Long projectId);

    // 사용중인 역할 조회
    List<AppProjectRole> findByProjectIdAndUseYnTrue(Long projectId);
}
