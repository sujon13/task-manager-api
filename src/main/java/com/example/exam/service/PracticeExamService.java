package com.example.exam.service;


import com.example.UserUtil;
import com.example.exam.enums.ExamStatus;
import com.example.exam.enums.ExamType;
import com.example.exam.model.Exam;
import com.example.exam.model.PrExamEditRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PracticeExamService {
    private final UserUtil userUtil;
    private final ExamService examService;

    @Transactional
    public Exam cancelPracticeExam(int id) {
        Exam practiceExam = examService.getExam(id);
        if (ExamType.PRACTICE.equals(practiceExam.getExamType()) && userUtil.hasEditPermission(practiceExam)) {
            throw new AccessDeniedException("You do not have permission to cancel the exam!");
        }

        if (ExamStatus.ENDED.equals(practiceExam.getStatus())) {
            throw new AccessDeniedException("This exam is already ended!");
        }

        practiceExam.setStatus(ExamStatus.CANCELLED);
        return practiceExam;
    }

    private boolean isReschedulePossible(Exam practiceExam) {
        return ExamStatus.NOT_SCHEDULED.equals(practiceExam.getStatus()) ||
                ExamStatus.NOT_STARTED.equals(practiceExam.getStatus());
    }

    @Transactional
    public Exam reschedulePracticeExam(int id, PrExamEditRequest editRequest) {
        Exam practiceExam = examService.getExam(id);
        if (ExamType.PRACTICE.equals(practiceExam.getExamType()) && userUtil.hasEditPermission(practiceExam)) {
            throw new AccessDeniedException("You do not have permission to reschedule the exam!");
        }

        if (isReschedulePossible(practiceExam)) {
            practiceExam.setStartTime(editRequest.getStartTime());
            if (editRequest.getAllocatedTimeInMin() != null) {
                practiceExam.setAllocatedTimeInMin(editRequest.getAllocatedTimeInMin());
            }
            practiceExam.setEndTime(practiceExam.getStartTime().plusMinutes(practiceExam.getAllocatedTimeInMin()));
        } else {
            throw new AccessDeniedException("Now reschedule is impossible!");
        }

        return practiceExam;
    }
}
