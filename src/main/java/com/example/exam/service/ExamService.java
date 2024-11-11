package com.example.exam.service;

import com.example.UserUtil;
import com.example.exam.enums.ExamStatus;
import com.example.exam.enums.ExamType;
import com.example.exam.model.*;
import com.example.exam.repository.ExamRepository;
import com.example.exception.NotFoundException;
import com.example.qa.model.QuesResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;


@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ExamService {
    private final ExamRepository examRepository;
    private final ExamQuesService examQuesService;
    private final ExamValidationService examValidationService;
    private final ExamStatusService examStatusService;
    private final UserUtil userUtil;
    private final UserExamRecordService userExamRecordService;
    private final ResultService resultService;

    @Transactional
    public Exam saveExam(Exam exam) {
        return examRepository.save(exam);
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
        if (!userUtil.hasEditPermission(exam)) {
            throw new AccessDeniedException("You do not have permission to edit this exam");
        }
        editExam(exam, request);
        return exam;
    }

    public Optional<Exam> findById(int id) {
        return examRepository.findById(id);
    }

    public Exam getExam(int id) {
        return findById(id)
                .orElseThrow(() ->  new NotFoundException("Exam not found with id " + id));
    }

    public List<Exam> findAll() {
        return examRepository.findAll();
    }

    public List<Exam> findAllLiveAndPracticeExams() {
        return examRepository.findAllByExamTypeIn(List.of(ExamType.LIVE, ExamType.PRACTICE));
    }

    public Page<Exam> findExams(Pageable pageable) {
        return examRepository.findAll(pageable);
    }

    public List<Exam> getRunningExams() {
        return examRepository.findAllByStatus(ExamStatus.RUNNING);
    }

    private void checkEntrancePermission(Exam exam) {
        if (!examStatusService.isExamRunning(exam)) {
            throw new AccessDeniedException("You can enter only in running exams");
        }

        if (!ExamType.isLiveOrPractice(exam.getExamType())) {
            throw new AccessDeniedException("You can enter only in live or practice exams");
        }

        if (ExamType.PRACTICE.equals(exam.getExamType()) && !userUtil.hasEditPermission(exam)) {
            throw new AccessDeniedException("You do not have permission to enter this exam");
        }
    }

    @Transactional
    public void enterExam(final int examId) {
        final Exam exam = getExam(examId);

        checkEntrancePermission(exam);

        userExamRecordService.enter(examId);
    }

    private void checkExitPermission(Exam exam) {
        if (!examStatusService.isExamRunning(exam)) {
            throw new AccessDeniedException("Exit is only possible from a running exam!");
        }

        if (!ExamType.isLiveOrPractice(exam.getExamType())) {
            throw new AccessDeniedException("Exit is only possible from a live or practice exam");
        }

        if (ExamType.PRACTICE.equals(exam.getExamType()) && !userUtil.hasEditPermission(exam)) {
            throw new AccessDeniedException("You do not have permission to exit from this exam");
        }
    }

    @Transactional
    public void exitFromExam(final int examId) {
        final Exam exam = getExam(examId);
        checkEntrancePermission(exam);

        userExamRecordService.exit(exam);

        if (ExamType.PRACTICE.equals(exam.getExamType()))
            resultService.updateMark(exam, userUtil.getUserName());
    }

    private ExamResponse buildExamResponse(Exam exam) {
        return ExamResponse.builder()
                .id(exam.getId())
                .name(exam.getName())
                .description(exam.getDescription())
                .startTime(exam.getStartTime())
                .allocatedTimeInMin(exam.getAllocatedTimeInMin())
                .endTime(exam.getEndTime())
                .status(exam.getStatus())
                .examType(exam.getExamType())
                .totalQuestions(exam.getTotalQuestions())
                .totalMarks(exam.getTotalMarks())
                .build();
    }

    private ExamQuesResponse buildExamQuesResponse(Exam exam, List<QuesResponse> questions) {
        return ExamQuesResponse.builder()
                .examResponse(buildExamResponse(exam))
                .questions(questions)
                .build();
    }

    public ExamQuesResponse getExamQuestions(int id, Pageable pageable) {
        Exam exam = getExam(id);
        if (userUtil.hasFetchPermission(exam)) {
            throw new AccessDeniedException("You do not have permission to attend the exam!");
        }

        List<QuesResponse> quesResponses = examQuesService.getQuestionList(exam.getParentId(), pageable);
        return buildExamQuesResponse(exam, quesResponses);
    }
}