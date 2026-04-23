package com.dobidan.bandeutseolap.domain.file.repository;

import com.dobidan.bandeutseolap.domain.file.entity.RelBoardFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface RelBoardFileRepository extends JpaRepository<RelBoardFile, Long> {

    // 게시글에 첨부된 파일 목록 조회
    List<RelBoardFile> findByBoardId(Long boardId);

    // 특정 게시글의 특정 파일 조회
    Optional<RelBoardFile> findByBoardIdAndFileId(Long boardId, Long fileId);

    // 특정 게시글의 파일 전체 삭제
    void deleteByBoardId(Long boardId);
}
