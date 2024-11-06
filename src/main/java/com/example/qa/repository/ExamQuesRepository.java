package com.example.qa.repository;

import com.example.qa.model.ExamQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface ExamQuesRepository extends JpaRepository<ExamQuestion, Integer> {
    List<ExamQuestion> findAllByQuestionId(Integer questionsId);

    List<ExamQuestion> findAllByExamId(Integer examId);

    boolean existsByQuestionIdAndExamId(Integer questionsId, Integer examId);
}
