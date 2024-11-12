package com.example.exam.repository;

import com.example.exam.enums.ExamType;
import com.example.exam.model.Exam;
import com.example.exception.NotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExamRepository extends JpaRepository<Exam, Integer> {
    Page<Exam> findAll(Pageable pageable);

    Page<Exam> findAllByExamType(ExamType examType, Pageable pageable);
    Page<Exam> findAllByExamTypeAndExaminee(ExamType examType, String examinee, Pageable pageable);
    List<Exam> findAllByExamTypeIn(List<ExamType> examTypes);

    default Exam getExam(int id) {
        return findById(id)
                .orElseThrow(() ->  new NotFoundException("Exam not found with id " + id));
    }

}