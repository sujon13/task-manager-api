package com.example.exam.service;

import com.example.exam.model.*;
import com.example.exam.repository.ExamQuesRepository;
import com.example.exam.repository.ExamRepository;
import com.example.exception.NotFoundException;
import com.example.qa.model.QuesResponse;
import com.example.qa.service.QuestionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    private final ExamRepository examRepository;
    private final QuestionService questionService;

    private ExamQuestion buildExamQuestion(ExamQuesRequest request) {
        ExamQuestion examQuestion = new ExamQuestion();
        examQuestion.setQuestionId(request.getQuestionId());
        examQuestion.setExamId(request.getExamId());
        if (request.getMarks() != null)
            examQuestion.setMarks(request.getMarks());
        examQuestion.setAns(request.getAns());
        return examQuestion;
    }

    @Transactional
    public List<ExamQuestion> createExamQuestions(List<ExamQuesRequest> examQuesRequests) {
        List<ExamQuestion> examQuestions = examQuesRequests.stream()
                .map(this::buildExamQuestion)
                .toList();
        try {
            examQuesRepository.saveAll(examQuestions);
            return examQuestions;
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }

    private void editExamQuestion(ExamQuestion examQuestion, ExamQuesEditRequest request) {
        if (request.getMarks() != null)
            examQuestion.setMarks(request.getMarks());
        if (request.getAns() != null)
            examQuestion.setAns(request.getAns());
    }

    @Transactional
    public ExamQuestion updateExamQuestion(final int id, ExamQuesEditRequest request) {
        Optional<ExamQuestion> examQuestion = examQuesRepository.findById(id);
        if (examQuestion.isEmpty())
            throw new NotFoundException("Exam question not found with id " + id);
        ExamQuestion examQuestionToEdit = examQuestion.get();

        editExamQuestion(examQuestionToEdit, request);
        return examQuestionToEdit;
    }

    public Optional<ExamQuestion> findById(int id) {
        return examQuesRepository.findById(id);
    }

    public Page<ExamQuestion> findAllByExamId(int examId, Pageable pageable) {
        return examQuesRepository.findAllByExamId(examId, pageable);
    }

    public boolean isQuesAlreadyChosenForThisExam(ExamQuestion request) {
        return examQuesRepository.existsByQuestionIdAndExamId(request.getQuestionId(), request.getExamId());
    }

    public int getAddedQuesCount(int examId) {
        return examQuesRepository.countByExamId(examId);
    }

    private ExamQuesResponse buildExamQuesResponse(Exam exam, List<QuesResponse> questions) {
        return ExamQuesResponse.builder()
                .id(exam.getId())
                .name(exam.getName())
                .description(exam.getDescription())
                .startTime(exam.getStartTime())
                .allocatedTimeInMin(exam.getAllocatedTimeInMin())
                .endTime(exam.getEndTime())
                .examType(exam.getExamType())
                .totalQuestions(exam.getTotalQuestions())
                .totalMarks(exam.getTotalMarks())
                .questions(questions)
                .build();
    }

    public ExamQuesResponse getExamQuestions(int examId, Pageable pageable) {
        Exam exam = examRepository.getExam(examId);
        Page<ExamQuestion> examQuestions = findAllByExamId(examId, pageable);

        List<Integer> quesIds = examQuestions.stream()
                .map(ExamQuestion::getQuestionId)
                .toList();

        List<QuesResponse> quesResponses = questionService.getQuesResponsesByIds(quesIds);
        return buildExamQuesResponse(exam, quesResponses);
    }

}
