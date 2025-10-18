package com.dobidan.bandeutseolap.domain.board.repository;

import com.dobidan.bandeutseolap.domain.board.domain.Board;
import com.dobidan.bandeutseolap.domain.board.domain.Favorite;
import com.dobidan.bandeutseolap.domain.board.domain.FavoritePk;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface FavoriteRepository extends JpaRepository<Favorite, FavoritePk> {
    Favorite findByBoardIdAndUserId(Long boardId, String userId);

    @Query(value =
        "SELECT F.BOARD_ID, " +
        "       F.USER_ID, " +
        "       M.USER_NAME " +
        "  FROM favorite F " +
        " INNER JOIN member M on M.USER_ID = F.USER_ID " +
        " WHERE F.BOARD_ID = ?1 ",
        nativeQuery = true
    )
    List<Favorite> findFavoriteUsers(Long boardId);

    @Transactional
    void deleteByBoardId(Long boardId);
}
