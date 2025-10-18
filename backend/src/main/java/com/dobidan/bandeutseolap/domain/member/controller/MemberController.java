package com.dobidan.bandeutseolap.domain.member.controller;


import com.dobidan.bandeutseolap.domain.member.dto.request.PostMemberRequestDto;
import com.dobidan.bandeutseolap.domain.member.dto.response.PostMemberResponseDto;
import com.dobidan.bandeutseolap.domain.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    
    // 유저 로그인
    // createNewMember
    @PostMapping("/member/login")
    public ResponseEntity<PostMemberResponseDto> createNewMember(
            @RequestBody PostMemberRequestDto dto
    ) {
        return memberService.addUser(dto);
    }
}
