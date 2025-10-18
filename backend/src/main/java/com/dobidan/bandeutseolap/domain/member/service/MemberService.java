package com.dobidan.bandeutseolap.domain.member.service;

import com.dobidan.bandeutseolap.domain.board.dto.response.PostBoardResponseDto;
import com.dobidan.bandeutseolap.domain.member.dto.request.PostMemberRequestDto;
import com.dobidan.bandeutseolap.domain.member.dto.response.PostMemberResponseDto;
import org.springframework.http.ResponseEntity;

public interface MemberService {

    // 유저 생성
    ResponseEntity<PostMemberResponseDto> addUser(PostMemberRequestDto dto);
}
