package com.dobidan.bandeutseolap.domain.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 비밀번호 재설정 2단계 요청 DTO
 *
 * 1단계에서 발급받은 resetToken과 새 비밀번호를 담는다.
 * 비밀번호 정책은 회원가입과 동일하게 적용한다.
 */
@Getter
@Setter
@NoArgsConstructor
public class ResetPasswordRequest {

    @NotBlank
    private String resetToken;

    @NotBlank
    @Size(min = 8, max = 50)
    private String newPassword;
}

