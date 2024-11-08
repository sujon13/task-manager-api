package com.example.exam.service;

import com.example.exam.model.Exam;
import com.example.exam.model.ExamEditRequest;
import com.example.exam.model.ExamRequest;
import com.example.exam.repository.ExamRepository;
import com.example.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;


@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ExamService {
    private final ExamRepository examRepository;
    private final ExamValidationService examValidationService;

    private Exam buildExam(ExamRequest request) {
        Exam exam = new Exam();
        exam.setName(request.getName());
        exam.setDescription(request.getDescription());
        exam.setStartTime(request.getStartTime());
        exam.setAllocatedTimeInMin(request.getAllocatedTimeInMin());
        if (exam.getStartTime() != null) {
            exam.setEndTime(exam.getStartTime().plusMinutes(exam.getAllocatedTimeInMin()));
        }
        if (request.getExamType() != null) {
            exam.setExamType(request.getExamType());
        }
        exam.setTotalQuestions(request.getTotalQuestions());
        exam.setTotalMarks(request.getTotalMarks());
        return exam;
    }

    @Transactional
    public Exam createExam(ExamRequest request) {
        Exam exam = buildExam(request);
        try {
            examRepository.save(exam);
            return exam;
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }

    private void updateQuesCount(Exam exam, ExamEditRequest request) {
        if (request.getTotalQuestions() < exam.getTotalQuestions()) {
            if (examValidationService.canTotalQuestionsBeReduced(exam.getId(), request.getTotalQuestions())) {
                exam.setTotalQuestions(request.getTotalQuestions());
            }
        } else {
            exam.setTotalQuestions(request.getTotalQuestions());
        }
    }

    private void editExam(Exam exam, ExamEditRequest request) {
        if (request.getName() != null)
            exam.setName(request.getName());
        if (request.getDescription() != null)
            exam.setDescription(request.getDescription());
        if (request.getStartTime() != null)
            exam.setStartTime(request.getStartTime());
        if (request.getAllocatedTimeInMin() != null) {
            exam.setAllocatedTimeInMin(request.getAllocatedTimeInMin());
            if (exam.getStartTime() != null) {
                exam.setEndTime(exam.getStartTime().plusMinutes(exam.getAllocatedTimeInMin()));
            }
        }

        if (request.getExamType() != null)
            exam.setExamType(request.getExamType());

        if (request.getTotalQuestions() != null)
            updateQuesCount(exam, request);

        if (request.getTotalMarks() != null)
            exam.setTotalMarks(request.getTotalMarks());
    }

    @Transactional
    public Exam updateExam(final int id, ExamEditRequest request) {
        Exam exam = getExam(id);
        editExam(exam, request);
        return exam;
    }

    public Optional<Exam> findById(int id) {
        return examRepository.findById(id);
    }

    public Exam getExam(int id) {
        Optional<Exam> optionalExam = examRepository.findById(id);
        if (optionalExam.isEmpty())
            throw new NotFoundException("Exam not found with id " + id);
        return optionalExam.get();
    }

    public Page<Exam> findExams(Pageable pageable) {
        return examRepository.findAll(pageable);
    }
}