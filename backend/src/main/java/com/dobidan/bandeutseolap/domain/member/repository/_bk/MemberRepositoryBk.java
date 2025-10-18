package com.dobidan.bandeutseolap.domain.member.repository._bk;


import com.dobidan.bandeutseolap.domain.member.domain.Member;

import java.util.List;
import java.util.Optional;

// DB, JPA .. 와 같은 저장소를 정하지 않았을 때, 인터페이스를 구현해놓고 개발하면 교체 편리.
// 1:40
public interface MemberRepositoryBk {
    //저장
    Member save(Member user);

    // 회원id로 회원 찾기
    Optional<Member> findById(Long id);

    // 회원 이름으로 회원 찾기
    Optional<Member> findByName(String name);

    // 회원 모든 리스트 반환
    List<Member> findAll();
}
