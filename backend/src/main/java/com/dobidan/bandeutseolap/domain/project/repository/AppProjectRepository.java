package com.dobidan.bandeutseolap.domain.project.repository;

import com.dobidan.bandeutseolap.domain.project.entity.AppProject;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * AppProjectRepository
 *
 * app_project 테이블 접근을 담당하는 레포지토리.
 * 프로젝트 코드 중복 체크, 담당자/상태별 조회 등을 제공한다.
 */

public interface AppProjectRepository extends JpaRepository<AppProject, Long> {

    // 프로젝트 코드로 조회
    Optional<AppProject> findByProjectCode(String projectCode);

    // 생성자로 조회
    List<AppProject> findByCreatedBy(Long createdBy);

    // 담당자로 조회
    List<AppProject> findByManagerUserId(Long managerUserId);

    // 상태로 조회
    List<AppProject> findByProjectStatusCd(String projectStatusCd);

    // 프로젝트 코드 중복 체크
    boolean existsByProjectCode(String projectCode);
}