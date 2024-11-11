package com.example.exam.service;

import com.example.exam.enums.ExamStatus;
import com.example.exam.model.Exam;
import com.example.exam.repository.ExamRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class ExamStatusService {
    private final ExamRepository examRepository;

    public void updateExamStatus(Exam exam) {
        if (exam.getStartTime() == null)
            exam.setStatus(ExamStatus.NOT_SCHEDULED);
        else if (exam.getStartTime().isAfter(LocalDateTime.now()))
            exam.setStatus(ExamStatus.NOT_STARTED);
        else if (exam.getEndTime().isAfter(LocalDateTime.now()))
            exam.setStatus(ExamStatus.RUNNING);
        else exam.setStatus(ExamStatus.ENDED);
    }

    public boolean isExamOver(Exam exam) {
        return ExamStatus.ENDED.equals(exam.getStatus());
    }

    public boolean isExamRunning(final Exam exam) {
        return ExamStatus.RUNNING.equals(exam.getStatus());
    }

    public boolean isExamRunning(final int examId) {
        Exam exam = examRepository.getExam(examId);
        return isExamRunning(exam);
    }
}