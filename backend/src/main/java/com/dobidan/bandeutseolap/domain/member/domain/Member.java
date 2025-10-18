package com.dobidan.bandeutseolap.domain.member.domain;

import com.dobidan.bandeutseolap.domain.member.dto.request.PostMemberRequestDto;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@NoArgsConstructor
//@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {

    @Column(name = "USER_ID")
    @Id //@GeneratedValue(strategy = GenerationType.IDENTITY)
    String userId;
    @Column(name = "USER_NAME")
    String name;
    @Column(name = "USER_EMAIL")
    String email;
    @Column(name = "USER_PASSWORD")
    String passwd;

    public Member(PostMemberRequestDto requestBody) {
        this.userId = requestBody.getUserId();
        this.name = requestBody.getUserName();
        this.email = requestBody.getUserEmail();
        this.passwd = requestBody.getPassWd();

    }
}
