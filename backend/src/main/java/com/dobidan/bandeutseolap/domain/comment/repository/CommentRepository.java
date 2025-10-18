package com.dobidan.bandeutseolap.domain.comment.repository;

import com.dobidan.bandeutseolap.domain.comment.domain.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<Comment,Long> {
}
