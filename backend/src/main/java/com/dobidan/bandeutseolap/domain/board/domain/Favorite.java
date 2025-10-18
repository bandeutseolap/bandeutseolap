package com.dobidan.bandeutseolap.domain.board.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@IdClass(FavoritePk.class)
@NoArgsConstructor
@AllArgsConstructor
public class Favorite {

    @Column(name = "BOARD_ID")
    @Id
    Long boardId;

    @Column(name = "USER_ID")
    @Id
    String userId;

    @Column(name = "USER_NAME")
    String userName;
}
