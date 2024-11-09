package com.example.exam.repository;

import com.example.exam.enums.ExamStatus;
import com.example.exam.model.PracticeExam;
import com.example.exception.NotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface PracticeExamRepository extends JpaRepository<PracticeExam, Integer> {
    Page<PracticeExam> findAll(Pageable pageable);

    List<PracticeExam> findAllByStatus(ExamStatus examStatus);

    default PracticeExam getPracticeExam(int id) {
        return findById(id)
                .orElseThrow(() ->  new NotFoundException("Practice Exam not found with id " + id));
    }

}