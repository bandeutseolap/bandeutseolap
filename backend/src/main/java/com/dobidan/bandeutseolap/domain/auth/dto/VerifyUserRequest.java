package com.dobidan.bandeutseolap.domain.auth.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class VerifyUserRequest {
    private String loginId;
    private String userName;
    private String email;
}
