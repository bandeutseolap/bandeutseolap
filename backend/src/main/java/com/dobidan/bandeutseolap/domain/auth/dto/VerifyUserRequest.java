package com.dobidan.bandeutseolap.domain.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 비밀번호 재설정 1단계 요청 DTO
 *
 * 비밀번호 찾기 시 본인 확인에 필요한 아이디/이름/이메일 정보를 담는다.
 */
@Getter
@Setter
@NoArgsConstructor
public class VerifyUserRequest {

    @NotBlank
    private String loginId;

    @NotBlank
    private String userName;

    @NotBlank
    @Email
    private String email;
}
