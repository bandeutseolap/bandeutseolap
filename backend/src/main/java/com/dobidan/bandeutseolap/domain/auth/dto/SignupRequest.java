package com.dobidan.bandeutseolap.domain.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

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
    private String loginId;

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

    private LocalDate birthDt;

    @Size(max = 30)
    private String jobCd;

    @Size(max = 10)
    private String countryCd;

    @Size(max = 255)
    private String imagePath;
}
