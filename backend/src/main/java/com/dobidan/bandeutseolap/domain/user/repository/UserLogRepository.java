package com.dobidan.bandeutseolap.domain.user.repository;

import com.dobidan.bandeutseolap.domain.user.entity.UserLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserLogRepository extends JpaRepository<UserLog, Long> {

}
