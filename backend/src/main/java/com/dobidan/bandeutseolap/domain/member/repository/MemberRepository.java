package com.dobidan.bandeutseolap.domain.member.repository;

import com.dobidan.bandeutseolap.domain.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

// 복잡한 동적 쿼리 : Querydsl 사용
// Jpa 제공하는 네이티브 쿼리 사용 가능  | JdbcTemplate, MyBatis 사용 가능
@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    boolean existsByUserId(String userId);
}
