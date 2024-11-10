package com.example.exam.repository;

import com.example.exam.model.UserExamRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserExamRecordRepository  extends JpaRepository<UserExamRecord, Integer> {
    Optional<UserExamRecord> findByExamIdAndExaminee(Integer examId, String examinee);
}