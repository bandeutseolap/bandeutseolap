package com.dobidan.bandeutseolap.domain.member.service;

import com.dobidan.bandeutseolap.domain.member.dto.request.PostMemberRequestDto;
import com.dobidan.bandeutseolap.domain.member.dto.response.PostMemberResponseDto;
import com.dobidan.bandeutseolap.domain.member.repository.MemberRepository;
import com.dobidan.bandeutseolap.domain.member.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

// 도메인, 레포지토리 활용
// ctrl + alt + v : 리턴 자동 선언
@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService{

    private final MemberRepository jpaMemberRepository;

    @Override
    public ResponseEntity<PostMemberResponseDto> addUser(PostMemberRequestDto requestBody) {
        Member member = new Member(requestBody);
        jpaMemberRepository.save(member);
        return PostMemberResponseDto.success();
    }

//    @Autowired
//    public MemberServiceImpl(JpaMemberRepository repository) {
//        this.repository = repository;
//    }


    //1. 회원가입 메소드
    //회원가입 시 유의 사항 비즈니스 작성
//    public Long join(Member user){
//        // 저장된 회원 아이디 있으면 에러 처리
//        repository.findById(user.getId()).ifPresent( u -> {
//                throw new IllegalStateException("이미 존재하는 회원입니다.");
//        } );
//
//        // 저장 후 아이디 리턴
//        repository.save(user);
//
//        return user.getId();
//    }

    
    //2. 전체 회원 조회 메소드
//    public List<Member> findAllMembers() {
//        return repository.findAll();
//    }
    
    
    //3. 1명 회원 조회 메소드
//    public Optional<Member> fineOneMember(Long userId){
//        // 아이디 찾고 리턴
//        return repository.findById(userId);
//    }


}
