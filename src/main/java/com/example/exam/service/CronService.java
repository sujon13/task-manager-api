package com.example.exam.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class CronService {
    private final ExamStatusService examStatusService;
    private final ExamService examService;
    private final ResultService resultService;
    private static final int PER_SECOND = 1000;

    @Scheduled(fixedRate = PER_SECOND)
    public void checkAndUpdateExamStatus() {
        examService.findAllLiveAndPracticeExams()
                .forEach(exam -> {
                    examStatusService.updateExamStatus(exam);
                    if (examStatusService.isExamOver(exam)) {
                        resultService.updateMark(exam);
                    }
                });
    }
}
