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

    public void updateExamStatus(Exam exam, ExamStatus status) {
        exam.setStatus(status);
    }

    public ExamStatus getUpdatedExamStatus(Exam exam) {
        if (exam.getStartTime() == null)
            return ExamStatus.NOT_SCHEDULED;
        else if (exam.getStartTime().isAfter(LocalDateTime.now()))
            return ExamStatus.NOT_STARTED;
        else if (exam.getEndTime().isAfter(LocalDateTime.now()))
            return ExamStatus.RUNNING;
        else return ExamStatus.ENDED;
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

    public boolean hasExamNotStarted(final Exam exam) {
        return ExamStatus.NOT_SCHEDULED.equals(exam.getStatus()) || ExamStatus.NOT_STARTED.equals(exam.getStatus());
    }
}