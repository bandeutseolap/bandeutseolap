package com.dobidan.bandeutseolap.domain.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 로그인 성공 시 클라이언트에 반환할 응답 DTO
 *
 * - JWT Access Token을 담아서 내려준다.
 */
@Getter
@AllArgsConstructor
public class LoginResponse {
    private String accessToken;
}
