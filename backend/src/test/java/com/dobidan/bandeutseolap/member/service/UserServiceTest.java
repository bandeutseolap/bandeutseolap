package com.dobidan.bandeutseolap.member.service;

import com.dobidan.bandeutseolap.domain.member.domain.Member;
import com.dobidan.bandeutseolap.domain.member.repository.MemoryMemberRepository;
import com.dobidan.bandeutseolap.domain.member.service.MemberServiceImpl;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;


// 1. 일단 서비스로 꺼내기
// 2. 레포지토리 이용해서 꺼내기
class UserServiceTest {
    // 서비스 호출
    MemberServiceImpl service;
    MemoryMemberRepository repository;
    
    // 레포지토리 호출 (단, 서비스에 있는 레포지토리와 동일 레포지토리 호출하기) => DI
    @BeforeEach
    void userService(){
        repository = new MemoryMemberRepository();
        service = new MemberServiceImpl(repository);

    }


    @AfterEach
    void clear(){
        repository.clearStore();
    }


    @Test
    void join() {
        Member user = new Member();

        //given : 데이터
        user.setName("12355");

        //when : 뭘 검증~?
        Long saveId = service.join(user);

        //then : 검증 결과(레포지토리 == 서비스 ?)
        Member findUser = service.fineOneMember(saveId).get();
        System.out.println("findUser :  " + findUser.getName());
        Assertions.assertThat(user.getName()).isEqualTo(findUser.getName());
    }

    // 중복 회원 가입 메소드 작성
//    void dupJoin(){
//
//    }


    @Test
    void findAllUsers() {
        //given
        Member user = new Member();
        user.setName("spring1");
        service.join(user);

        Member user2 = new Member();
        user2.setName("spring2");
        service.join(user2);

        //when
        List<Member> findAll = service.findAllMembers();

        //then
        Assertions.assertThat(findAll).contains(user, user2);

    }

    @Test
    void fineOneUser() {
    }
}