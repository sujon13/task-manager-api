package com.example.qa.repository;

import com.example.qa.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Integer> {
    List<Comment> findAllByQuestionId(int questionId);

    void deleteAllByQuestionId(int questionId);
}
