package com.example.exam.service;

import com.example.exam.model.Exam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
@RequiredArgsConstructor
@Slf4j
public class CronService {
    private final ExamService examService;
    private final ExamCronService examCronService;
    private static final int PER_SECOND = 10000;

    @Scheduled(fixedDelay = PER_SECOND)
    public void checkAndUpdateExamStatus() {
        //ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();
        ExecutorService executor = Executors.newFixedThreadPool(10);
        examService.findAllLiveAndPracticeExams()
                .stream()
                .map(Exam::getId)
                .forEach(examId -> {
                    executor.submit(() -> {
                        examCronService.updateExamAndQuestions(examId);
                    });
                });
    }
}
