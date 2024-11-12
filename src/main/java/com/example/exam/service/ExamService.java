package com.example.exam.service;

import com.example.UserUtil;
import com.example.exam.enums.ExamType;
import com.example.exam.model.Exam;
import com.example.exam.repository.ExamRepository;
import com.example.exception.NotFoundException;
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
    private final ExamStatusService examStatusService;
    private final UserUtil userUtil;
    private final UserExamRecordService userExamRecordService;
    private final ResultService resultService;

    @Transactional
    public Exam saveExam(Exam exam) {
        return examRepository.save(exam);
    }

    public Optional<Exam> findById(int id) {
        return examRepository.findById(id);
    }

    public Exam getExam(int id) {
        return findById(id)
                .orElseThrow(() ->  new NotFoundException("Exam not found with id " + id));
    }

    public List<Exam> findAllByExamTypeIn(List<ExamType> examTypeList) {
        return examRepository.findAllByExamTypeIn(examTypeList);
    }

    public List<Exam> findAllLiveAndPracticeExams() {
        return findAllByExamTypeIn(List.of(ExamType.LIVE, ExamType.PRACTICE));
    }

    public Page<Exam> findExams(ExamType examType, Pageable pageable) {
        if (examType.isPractice()) {
            return examRepository.findAllByExamTypeAndExaminee(examType, userUtil.getUserName(), pageable);
        } else {
            return examRepository.findAllByExamType(examType, pageable);
        }
    }

    private void checkEntrancePermission(Exam exam) {
        if (!examStatusService.isExamRunning(exam)) {
            throw new AccessDeniedException("You can enter only in running exams");
        }

        if (!exam.getExamType().isLiveOrPractice()) {
            throw new AccessDeniedException("You can enter only in live or practice exams");
        }

        if (exam.getExamType().isPractice() && !userUtil.hasEditPermission(exam)) {
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

        if (!exam.getExamType().isLiveOrPractice()) {
            throw new AccessDeniedException("Exit is only possible from a live or practice exam");
        }

        if (!userExamRecordService.hasUserEnteredTheExam(exam.getId())) {
            throw new AccessDeniedException("You have not entered the exam!");
        }
    }

    @Transactional
    public void exitFromExam(final int examId) {
        final Exam exam = getExam(examId);
        checkExitPermission(exam);

        userExamRecordService.exit(exam);

        if (exam.getExamType().isPractice())
            resultService.updateMark(exam, userUtil.getUserName());
    }
}