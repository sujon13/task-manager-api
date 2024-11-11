package com.example.exam.service;

import com.example.exam.repository.ExamQuesRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ExamValidationService {
    private final ExamQuesRepository examQuesRepository;

    private int getAddedQuesCount(int examId) {
        return examQuesRepository.countByExamId(examId);
    }

    public boolean canTotalQuestionsBeReduced(int examId, int proposedCount) {
        int existingQuesCount = getAddedQuesCount(examId);
        return existingQuesCount < proposedCount;
    }
}
