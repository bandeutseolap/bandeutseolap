package com.dobidan.bandeutseolap.global.common;

public interface ResponseMessage {
    String SUCCESS = "Success.";
    String NOT_EXIST_USER = "존재하지 않는 유저입니다.";
    String NOT_ACCEPTABLE =
            "컨트롤러가 JSON이 아니라 뷰/문자열을 반환하고 있습니다.";

    //String NOT_EXIST_BOARDS = "게시물이 없습니다.";
    String NOT_EXIST_BOARD = "존재하지 않는 게시물입니다.";
    
    String NO_PERMISSION = "권한이 없습니다";
}
