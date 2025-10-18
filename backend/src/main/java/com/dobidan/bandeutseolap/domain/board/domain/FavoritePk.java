package com.dobidan.bandeutseolap.domain.board.domain;


import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@EqualsAndHashCode
@Getter
@Setter
// 복합키 클래스 생성
public class FavoritePk implements Serializable {

    String userId;
    Long boardId;

}
