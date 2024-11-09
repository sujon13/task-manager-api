package com.example.exam.repository;

import com.example.exam.model.ExamQuestion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface ExamQuesRepository extends JpaRepository<ExamQuestion, Integer> {
    List<ExamQuestion> findAllByQuestionId(Integer questionsId);

    Page<ExamQuestion> findAllByExamId(Integer examId, Pageable pageable);

    int countByExamId(Integer examId);

    boolean existsByQuestionIdAndExamId(Integer questionsId, Integer examId);

    Optional<ExamQuestion> findByExamIdAndQuestionId(Integer examId, Integer questionId);
}
