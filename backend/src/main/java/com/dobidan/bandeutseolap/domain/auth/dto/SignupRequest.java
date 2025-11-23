package com.dobidan.bandeutseolap.domain.auth.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * 회원가입 요청 DTO
 *
 * 클라이언트에서 전달하는 회원가입 정보(username, password)를 담는다.
 */
@Getter
@Setter
public class SignupRequest {

    private String username;  // 로그인 아이디
    private String password;  // 평문 비밀번호 (서버에서 암호화)

}
