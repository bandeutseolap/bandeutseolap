package com.dobidan.bandeutseolap.domain.board.repository;

import com.dobidan.bandeutseolap.domain.board.domain.Board;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

// 클래스 상속
@Repository
public interface BoardRepository extends JpaRepository<Board, Long> {

    // 조인 테이블 가져오기(jpql vs sql) - 43강 16:00~
    @Query(value =
            "SELECT B.BOARD_ID,\n" +
            "       B.BOARD_TTL,\n" +
            "       B.BOARD_CONT,\n" +
            "       B.COMMENT_CNT,\n" +
            "       B.VIEW_CNT,\n" +
            "       B.OPEN_YN,\n" +
            "       B.CRAT_EMP,\n" +
            "       B.CRAT_DT\n" +
            "  FROM Board B\n" +
            " INNER JOIN Member M ON M.USER_ID = B.CRAT_EMP\n" +
            " WHERE B.BOARD_ID = ?1",
            nativeQuery = true
    )
    Board selectBoardDetail(Long boardId);


}
