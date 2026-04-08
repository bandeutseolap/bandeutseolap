package com.dobidan.bandeutseolap.domain.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
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

    @NotBlank
    @Size(max = 50)
    private String lgnId;

    @NotBlank
    @Size(min = 8,max = 50)
    private String password;

    @NotBlank
    @Size(max = 100)
    private String userName;

    @NotBlank
    @Email
    private String email;

    @Size(max = 40)
    private String mobilePhone;
}
