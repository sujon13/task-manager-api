package com.example.exam.repository;

import com.example.exam.entity.ExamTaker;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExamTakerRepository extends JpaRepository<ExamTaker, Integer> {
}