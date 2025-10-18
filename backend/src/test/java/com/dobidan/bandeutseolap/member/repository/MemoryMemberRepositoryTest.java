package com.dobidan.bandeutseolap.member.repository;

import com.dobidan.bandeutseolap.domain.member.domain.Member;
import com.dobidan.bandeutseolap.domain.member.repository.MemoryMemberRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class MemoryMemberRepositoryTest {


    //레포지토리
    MemoryMemberRepository repository = new MemoryMemberRepository();

    @AfterEach
    public void afterEach(){
        repository.clearStore();
    }

    @Test
    public void save(){
        // 데이터 받아오기
        Member user = new Member();
        user.setName("yerin");
        // 저장 검증
        repository.save(user);

        // id 로 값 가져오는지 검증
        Member result = repository.findById(user.getId()).get();
        // 기대값, 실제값
        Assertions.assertEquals(user, result);

    }


    @Test
    public void findByName() {
        Member user = new Member();
        user.setName("yerin");
        repository.save(user);


        Member user2 = new Member();
        user2.setName("yerin2");
        repository.save(user2);
        Member result = repository.findByName("yerin2").get();

        // 실제 값
        assertThat(result).isEqualTo(user2);
        //Assertions.assertEquals(user, result);
    }

    @Test
    public void findAll(){
        Member user = new Member();
        user.setName("yerin2");
        repository.save(user);

        Member user2 = new Member();
        user2.setPasswd("1234");
        repository.save(user2);

        List<Member> result = repository.findAll();
        System.out.println(" 1 : " + result.toString());
        assertThat(result.size()).isEqualTo(2);

    }
}
