package com.example.exam.service;


import com.example.UserUtil;
import com.example.exam.enums.ExamStatus;
import com.example.exam.enums.ExamType;
import com.example.exam.model.Exam;
import com.example.exam.model.ExamEditRequest;
import com.example.exam.model.PrExamEditRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class ExamUpdateService {
    private final UserUtil userUtil;
    private final ExamService examService;
    private final ExamValidationService examValidationService;

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
    public Exam updateExam(final int id, final ExamEditRequest request) {
        Exam exam = examService.getExam(id);
        if (!userUtil.hasEditPermission(exam)) {
            throw new AccessDeniedException("You do not have permission to edit this exam");
        }
        editExam(exam, request);
        return exam;
    }

    private void checkCancelPermission(final Exam exam) {
        if (!exam.getExamType().isLiveOrPractice()) {
            throw new AccessDeniedException("Only live and practice exams can be cancelled!");
        }

        if (ExamStatus.ENDED.equals(exam.getStatus())) {
            throw new AccessDeniedException("This exam is already ended!");
        }

        if (!userUtil.hasEditPermission(exam)) {
            throw new AccessDeniedException("You do not have permission to cancel the exam!");
        }
    }

    public Exam cancelExam(final int examId) {
        Exam exam = examService.getExam(examId);
        checkCancelPermission(exam);
        exam.setStatus(ExamStatus.CANCELLED);
        return exam;
    }

    private boolean isReschedulePossible(final Exam exam) {
        return ExamStatus.NOT_SCHEDULED.equals(exam.getStatus()) ||
                ExamStatus.NOT_STARTED.equals(exam.getStatus());
    }

    private void reschedule(final Exam exam, final PrExamEditRequest editRequest) {
        exam.setStartTime(editRequest.getStartTime());
        if (editRequest.getAllocatedTimeInMin() != null) {
            exam.setAllocatedTimeInMin(editRequest.getAllocatedTimeInMin());
        }
        exam.setEndTime(exam.getStartTime().plusMinutes(exam.getAllocatedTimeInMin()));
    }

    private void checkReschedulePermission(final Exam exam) {
        if (!userUtil.hasEditPermission(exam)) {
            throw new AccessDeniedException("You do not have permission to reschedule the exam!");
        }

        if (!isReschedulePossible(exam)) {
            throw new AccessDeniedException("Now reschedule is impossible!");
        }
    }

    public Exam rescheduleExam(final int examId, final PrExamEditRequest editRequest) {
        Exam exam = examService.getExam(examId);
        checkReschedulePermission(exam);
        reschedule(exam, editRequest);
        return exam;
    }
}
