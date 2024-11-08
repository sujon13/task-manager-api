package com.example.exam.repository;

import com.example.exam.model.Exam;
import com.example.exception.NotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ExamRepository extends JpaRepository<Exam, Integer> {
    Page<Exam> findAll(Pageable pageable);

    default Exam getExam(int id) {
        Optional<Exam> optionalExam = findById(id);
        if (optionalExam.isEmpty())
            throw new NotFoundException("Exam not found with id " + id);
        return optionalExam.get();
    }

}