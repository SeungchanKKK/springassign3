package com.sparta.springassign2.repository;

import com.sparta.springassign2.model.Comments;
import com.sparta.springassign2.model.Memo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentsRepository extends JpaRepository<Comments, Long> {
    List<Comments> findAllByOrderByModifiedAtDesc();

}
