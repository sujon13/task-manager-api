package com.example.exam.repository;

import com.example.exam.enums.ExamStatus;
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

    List<Exam> findAllByStatus(ExamStatus status);

    default Exam getExam(int id) {
        return findById(id)
                .orElseThrow(() ->  new NotFoundException("Exam not found with id " + id));
    }

}