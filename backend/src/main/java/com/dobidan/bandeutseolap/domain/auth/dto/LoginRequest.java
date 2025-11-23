package com.dobidan.bandeutseolap.domain.auth.dto;


import lombok.Getter;
import lombok.Setter;

/**
 * 로그인 요청 DTO
 *
 * 클라이언트에서 전달하는 로그인 정보(username, password)를 담는다.
 */
@Getter
@Setter
public class LoginRequest {
    private String username;
    private String password;
}
