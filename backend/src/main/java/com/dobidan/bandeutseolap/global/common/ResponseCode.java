package com.dobidan.bandeutseolap.global.common;

// 10강
// 인터페이스는 "public static final"로 인식하기 때문에 생략하고 작성 가능
public interface ResponseCode {
    // HTTP 상태코드 및 메시지 정의
    String SUCCESS = "SUCCESS";
    String NOT_EXIST_USER = "NOT_EXIST_USER";
    String NOT_ACCEPTABLE = "NOT_ACCEPTABLE";
    String NOT_EXIST_BOARD = "NOT_EXIST_BOARD";
    String NO_PERMISSION = "NO_PERMISSION";
}
