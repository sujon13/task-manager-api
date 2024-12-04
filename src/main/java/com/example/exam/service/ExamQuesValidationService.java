package com.example.exam.service;

import com.example.exam.entity.Exam;
import com.example.exam.repository.ExamRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ExamQuesValidationService {
    private final ExamQuesService examQuesService;
    private final ExamRepository examRepository;

    public boolean canQuesBeAdded(int examId, int newQuestionCount) {
        Exam exam = examRepository.getExam(examId);

        if (exam.getTotalQuestions() == null)
            return true;

        int addedQuestionCount = examQuesService.getCurrentQuesCount(examId);
        return addedQuestionCount + newQuestionCount <= exam.getTotalQuestions();
    }
}
