package com.dobidan.bandeutseolap.domain.board.dto.object;

import com.dobidan.bandeutseolap.domain.board.domain.Board;
import com.dobidan.bandeutseolap.domain.board.domain.Favorite;
import com.dobidan.bandeutseolap.domain.board.dto.response.GetFavoriteResponseDto;
import com.dobidan.bandeutseolap.domain.board.repository.FavoriteRepository;
import com.dobidan.bandeutseolap.domain.member.domain.Member;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class FavoriteListItem {
    private String userId;
    private String userName;

    public FavoriteListItem(Favorite favorite) {
        this.userId = favorite.getUserId();
        this.userName = favorite.getUserName();
    }


    public static List<FavoriteListItem> favoriteList(List<Favorite> favorites) {
        List<FavoriteListItem> list = new ArrayList<>();
        for(Favorite favorite : favorites) {
            FavoriteListItem favoriteListItem = new FavoriteListItem(favorite);
            list.add(favoriteListItem);
        }
        return list;
    }

}
