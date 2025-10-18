package com.dobidan.bandeutseolap.domain.member.dto.response;

import com.dobidan.bandeutseolap.global.common.ResponseCode;
import com.dobidan.bandeutseolap.global.common.ResponseMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PostMemberResponseDto implements ResponseCode, ResponseMessage {
    String code;
    String message;

    public static ResponseEntity<PostMemberResponseDto> success() {
        PostMemberResponseDto result = new PostMemberResponseDto(ResponseCode.SUCCESS, ResponseMessage.SUCCESS);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }
}
