package com.dobidan.bandeutseolap.domain.auth.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ResetPasswordRequest {
    private String loginId;
    private String userName;
    private String email;
    private String newPassword;

    // 테스트나 빌더 대용으로 사용할 생성자
    public ResetPasswordRequest(String loginId, String userName, String email, String newPassword) {
        this.loginId = loginId;
        this.userName = userName;
        this.email = email;
        this.newPassword = newPassword;
    }
}
