package com.example.exam.repository;

import com.example.exam.entity.Result;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface ResultRepository extends JpaRepository<Result, Integer> {
    Optional<Result> findByExamIdAndExaminee(int examId, String examinee);
}