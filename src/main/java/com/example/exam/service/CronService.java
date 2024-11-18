package com.example.exam.service;

import com.example.exam.enums.ExamStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@RequiredArgsConstructor
@Slf4j
public class CronService {
    private final ExamService examService;
    private final ExamCronService examCronService;
    private final ExamStatusService examStatusService;
    private static final int PER_SECOND = 10000;

    @Scheduled(fixedDelay = PER_SECOND)
    public void checkAndUpdateExamStatus() {
        log.debug("{} thread is checking exam status at {}", Thread.currentThread().getName(), new Date());
        examService.findLiveAndPracticeExams()
                .forEach(exam -> {
                    ExamStatus previousExamStatus = exam.getStatus();
                    ExamStatus updatedExamStatus = examStatusService.getUpdatedExamStatus(exam);
                    if (updatedExamStatus != previousExamStatus) {
                        examCronService.updateExamAndQuestions(exam.getId(), updatedExamStatus);
                    }
                });
    }
}
