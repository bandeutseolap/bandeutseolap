package com.dobidan.bandeutseolap.domain.user.repository;

import com.dobidan.bandeutseolap.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * UserRepository
 *
 * - User 엔티티에 접근하는 DAO 역할
 * - JpaRepository<User, Long> 을 상속하면 CRUD 메서드 자동 제공됨
 *   예)
 *     findAll()
 *     save()
 *     findById()
 *     deleteById()
 *
 * - findByUsername()
 *   → 로그인 처리할 때 username으로 사용자 조회하는 데 필요
 *   → Optional 반환: 값이 없을 때 NPE를 피함
 */

public interface UserRepository extends JpaRepository<User, Long> {

        Optional<User> findByUsername(String username);  // select * from users where username = ?
}

