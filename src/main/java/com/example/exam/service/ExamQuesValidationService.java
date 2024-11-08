package com.example.exam.service;

import com.example.exam.model.Exam;
import com.example.exam.repository.ExamRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

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

        int addedQuestionCount = examQuesService.getAddedQuesCount(examId);
        return addedQuestionCount + newQuestionCount <= exam.getTotalQuestions();
    }
}
