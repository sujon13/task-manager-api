package com.example.exam.repository;

import com.example.exam.model.Submission;
import com.example.exception.NotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubmissionRepository extends JpaRepository<Submission, Integer> {
    Page<Submission> findAll(Pageable pageable);

    List<Submission> findAllByExamIdAndExamineeUserName(Integer examId, String userName);

    default Submission getSubmission(int id) {
        return findById(id)
                .orElseThrow(() ->  new NotFoundException("Submission not found with id " + id));
    }
}