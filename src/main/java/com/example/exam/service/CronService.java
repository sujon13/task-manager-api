package com.example.exam.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class CronService {
    private final PracticeExamService practiceExamService;
    private static final int PER_MINUTE = 60 * 1000;

    @Scheduled(fixedRate = PER_MINUTE)
    public void checkExamEndingStatus() {
        log.info("checking exam ending status at {}", LocalDateTime.now());
        practiceExamService.checkExamEndingStatus();
    }
}
