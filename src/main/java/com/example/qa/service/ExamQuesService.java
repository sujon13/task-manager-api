package com.example.qa.service;

import com.example.qa.model.ExamQuesRequest;
import com.example.qa.model.ExamQuestion;
import com.example.qa.repository.ExamQuesRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ExamQuesService {
    private final ExamQuesRepository examQuesRepository;

    private ExamQuestion buildExamQuestion(ExamQuesRequest request) {
        ExamQuestion examQuestion = new ExamQuestion();
        examQuestion.setQuestionId(request.getQuestionId());
        examQuestion.setExamId(request.getExamId());
        return examQuestion;
    }

    @Transactional
    public ExamQuestion createExamQuestion(ExamQuesRequest request) {
        ExamQuestion frequency = buildExamQuestion(request);
        try {
            examQuesRepository.save(frequency);
            return frequency;
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }

    public Optional<ExamQuestion> findById(int id) {
        return examQuesRepository.findById(id);
    }

    public List<ExamQuestion> findByQuestionId(int questionId) {
        return examQuesRepository.findAllByQuestionId(questionId);
    }

    public boolean isQuesAlreadyChosenForThisExam(ExamQuestion request) {
        return examQuesRepository.existsByQuestionIdAndExamId(request.getQuestionId(), request.getExamId());
    }
}
