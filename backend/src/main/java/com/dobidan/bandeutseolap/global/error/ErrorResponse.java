package com.dobidan.bandeutseolap.global.error;

import lombok.Getter;
import java.time.LocalDateTime;

@Getter
public class ErrorResponse {
    private final LocalDateTime timestamp = LocalDateTime.now();
    private final int status;
    private final String error;
    private String message;

    public ErrorResponse(int status, String error, String message){
        this.status = status;
        this.error = error;
        this.message = message;
    }
}
