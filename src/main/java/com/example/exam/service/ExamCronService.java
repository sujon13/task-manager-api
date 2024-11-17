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
    public void updateExamAndQuestions(final int examId) {
        Exam exam = examService.getExam(examId);
        System.out.println("exam: " + exam.getId() + " running in virtual thread: " + Thread.currentThread());

        ExamStatus previousExamStatus = exam.getStatus();
        examStatusService.updateExamStatus(exam);

        if (ExamStatus.RUNNING.equals(previousExamStatus) && examStatusService.isExamOver(exam)) {
            System.out.println("exam: " + exam.getId() + " is over");
            resultService.updateMarkAndMeritPosition(exam);
            examQuesService.makeExamQuestionsVisible(exam);
        }
    }
}
