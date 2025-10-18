package com.dobidan.bandeutseolap.domain.member.config;

import com.dobidan.bandeutseolap.domain.member.repository.MemberRepository;
import com.dobidan.bandeutseolap.domain.member.service.MemberServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// 메모리 -> db 로 바꿔도 config 파일만 수정하면 됨 (장점)
// 어노테이션 사용 시, 해당 파일들 다 수정해야 한다는 단점이 있음
@Configuration
public class MemberSpringConfig {

    private final MemberRepository memberRepository;

    @Autowired
    public MemberSpringConfig(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Bean
    public MemberServiceImpl memberService() {
        return new MemberServiceImpl(memberRepository);
    }

//    @Bean
//    public MemberRepository memberRepository() {
//        return new MemberRepository();
//    }
}
