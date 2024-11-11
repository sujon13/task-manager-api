package com.example.exam.service;

import com.example.UserUtil;
import com.example.exam.enums.ExamType;
import com.example.exam.model.Exam;
import com.example.exam.model.ExamAddRequest;
import com.example.exam.model.ExamCloneRequest;
import com.example.exam.model.ExamQuestion;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ExamAddService {
    private final ExamQuesService examQuesService;
    private final ExamStatusService examStatusService;
    private final UserUtil userUtil;
    private final ExamService examService;

    private void addExamTimeAndStatus(Exam exam, ExamAddRequest request) {
        exam.setStartTime(request.getStartTime());
        exam.setAllocatedTimeInMin(request.getAllocatedTimeInMin());
        if (exam.getStartTime() != null) {
            exam.setEndTime(exam.getStartTime().plusMinutes(exam.getAllocatedTimeInMin()));
            examStatusService.updateExamStatus(exam);
        }
    }

    private void checkSecurity(ExamType examType) {
        if (ExamType.PRACTICE.equals(examType)) {
            throw new AccessDeniedException("Practice exam can only be cloned");
        } else if (ExamType.REAL.equals(examType)) {
            if (!userUtil.isAdmin()) {
                throw new AccessDeniedException("Only admins can add real exams");
            }
        } else {
            if (!userUtil.hasAnyRole("EXAMINER", "ADMIN")) {
                throw new AccessDeniedException("Only admins or examiners can add " + examType + " exams");
            }
        }
    }

    private Exam buildExam(ExamAddRequest request) {
        Exam exam = new Exam();
        exam.setName(request.getName());
        exam.setDescription(request.getDescription());
        addExamTimeAndStatus(exam, request);

        exam.setExamType(request.getExamType());

        exam.setTotalQuestions(request.getTotalQuestions());
        exam.setTotalMarks(request.getTotalMarks());
        return exam;
    }

    @Transactional
    public Exam addExam(ExamAddRequest request) {
        checkSecurity(request.getExamType());
        Exam exam = buildExam(request);
        return examService.saveExam(exam);
    }

    private Exam cloneExam(Exam parentExam, ExamCloneRequest cloneRequest) {
        Exam exam = new Exam();
        BeanUtils.copyProperties(parentExam, exam, "id");

        exam.setParentId(parentExam.getId());

        exam.setStartTime(cloneRequest.getStartTime());
        exam.setAllocatedTimeInMin(cloneRequest.getAllocatedTimeInMin());
        exam.setEndTime(exam.getStartTime().plusMinutes(exam.getAllocatedTimeInMin()));
        exam.setExamType(cloneRequest.getExamType());

        examStatusService.updateExamStatus(exam);

        if (cloneRequest.getTotalQuestions() != null) {
            exam.setTotalQuestions(cloneRequest.getTotalQuestions());
        }
        if (cloneRequest.getTotalMarks() != null) {
            exam.setTotalMarks(cloneRequest.getTotalMarks());
        }

        if (ExamType.PRACTICE.equals(exam.getExamType())) {
            exam.setExaminee(userUtil.getUserName());
        }
        return exam;
    }

    private void checkExamTypeValidity(ExamType parentExamType, ExamType childExamType) {
        if (ExamType.PRACTICE.equals(parentExamType)) {
            throw new AccessDeniedException("Practice exam cannot be cloned!");
        }

        if (ExamType.SAMPLE.equals(childExamType) || ExamType.REAL.equals(childExamType)) {
            final String errorMessage = " exam cannot be created by cloning!";
            throw new AccessDeniedException(childExamType.name() + errorMessage);
        }
    }

    private void checkClonedExamSecurity(ExamType clonedExamType) {
        if (ExamType.PRACTICE.equals(clonedExamType)) {
            if (!userUtil.hasAnyRole("USER")) {
                throw new AccessDeniedException("Only users can create practice exams");
            }
        } else if (ExamType.LIVE.equals(clonedExamType)) {
            if (!userUtil.hasAnyRole("EXAMINER", "ADMIN")) {
                throw new AccessDeniedException("Only admins or examiners can create live exams");
            }
        }
    }

    private void cloneExamQuestions(int fromExamId, int toExamId) {
        List<ExamQuestion> examQuestions = examQuesService.findAllByExamId(fromExamId);
        List<ExamQuestion> modifiedExamQuestions = examQuestions
                .stream()
                .peek(examQuestion -> {
                    examQuestion.setId(null);
                    examQuestion.setExamId(toExamId);
                })
                .toList();
        examQuesService.saveExamQuestions(modifiedExamQuestions);
    }

    @Transactional
    public Exam cloneExam(int parentId, ExamCloneRequest cloneRequest) {
        Exam parentExam = examService.getExam(parentId);

        checkExamTypeValidity(parentExam.getExamType(), cloneRequest.getExamType());
        checkClonedExamSecurity(cloneRequest.getExamType());

        Exam clonedExam = cloneExam(parentExam, cloneRequest);
        examService.saveExam(clonedExam);

        cloneExamQuestions(clonedExam.getParentId(), clonedExam.getId());
        return clonedExam;
    }

}