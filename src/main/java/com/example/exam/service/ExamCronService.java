package com.example.exam.service;

import com.example.exam.enums.ExamStatus;
import com.example.exam.model.Exam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class ExamCronService {
    private final ExamStatusService examStatusService;
    private final ResultService resultService;
    private final ExamQuesService examQuesService;
    private final ExamService examService;

    @Transactional
    public void updateExamAndQuestions(final int examId, final ExamStatus updatedExamStatus) {
        Exam exam = examService.getExam(examId);
        ExamStatus previousExamStatus = exam.getStatus();

        log.info("New status of exam {} is {}", exam.getId(), updatedExamStatus);
        examStatusService.updateExamStatus(exam, updatedExamStatus);

        if (ExamStatus.RUNNING.equals(previousExamStatus) && examStatusService.isExamOver(exam)) {
            log.info("exam: {} is over", exam.getId());
            resultService.updateMarkAndMeritPosition(exam);
            examQuesService.makeExamQuestionsVisible(exam.getId());
        }
    }
}
