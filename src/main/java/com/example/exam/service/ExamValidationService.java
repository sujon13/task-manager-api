package com.example.exam.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ExamValidationService {
    private final ExamQuesService examQuesService;

    public boolean canTotalQuestionsBeReduced(int examId, int proposedCount) {
        int existingQuesCount = examQuesService.getAddedQuesCount(examId);
        return existingQuesCount < proposedCount;
    }
}
