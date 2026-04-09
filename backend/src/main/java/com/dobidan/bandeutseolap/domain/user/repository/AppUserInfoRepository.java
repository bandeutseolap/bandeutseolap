package com.dobidan.bandeutseolap.domain.user.repository;

import com.dobidan.bandeutseolap.domain.user.entity.AppUserInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AppUserInfoRepository extends JpaRepository<AppUserInfo,String> {

    //userId로 조회
    Optional<AppUserInfo> findByAppUser_Id(Long userId);

}
