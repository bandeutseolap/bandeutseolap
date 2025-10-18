package com.dobidan.bandeutseolap.domain.member.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PostMemberRequestDto {

    @NotBlank
    String userId;

    @NotBlank
    String userName;

    @NotBlank
    String userEmail;

    @NotBlank
    String passWd;
}
