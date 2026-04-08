package com.dobidan.bandeutseolap.domain.user.repository;

import com.dobidan.bandeutseolap.domain.user.entity.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AppUserRepository extends JpaRepository<AppUser,Long> {

    // 로그인 아이디로 조회
        Optional<AppUser> findByLgnId(String lgnId);
    // 이메일로 조회
        Optional<AppUser> findByEmail(String email);
    // 로그인 아이디 중복 체크
        boolean existsByLgnId(String lgnId);
    // 이메일 중복 체크
        boolean existsByEmail(String email);
}
