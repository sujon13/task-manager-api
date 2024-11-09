package com.example.exam.service;


import com.example.UserUtil;
import com.example.exam.enums.ExamStatus;
import com.example.exam.model.*;
import com.example.exam.repository.PracticeExamRepository;
import com.example.exception.NotFoundException;
import com.example.qa.model.QuesResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PracticeExamService {
    private final PracticeExamRepository practiceExamRepository;
    private final UserUtil userUtil;
    private final ExamService examService;
    private final ExamQuesService examQuesService;
    private final SubmissionService submissionService;

    private void setExamStatus(PracticeExam exam) {
        if (exam.getStartTime() == null)
            exam.setStatus(ExamStatus.NOT_SCHEDULED);
        else if (exam.getStartTime().isAfter(LocalDateTime.now()))
            exam.setStatus(ExamStatus.NOT_STARTED);
        else if (exam.getEndTime().isAfter(LocalDateTime.now()))
            exam.setStatus(ExamStatus.RUNNING);
        else exam.setStatus(ExamStatus.ENDED);
    }

    private void updateExamStatus(PracticeExam exam) {
        setExamStatus(exam);
    }

    private PracticeExam buildPracticeExam(Exam exam, PrExamAddRequest addRequest) {
        PracticeExam practiceExam = new PracticeExam();
        BeanUtils.copyProperties(exam, practiceExam, "id");

        practiceExam.setParentExamId(exam.getId());

        practiceExam.setStartTime(addRequest.getStartTime());
        if (addRequest.getAllocatedTimeInMin() != null) {
            practiceExam.setAllocatedTimeInMin(addRequest.getAllocatedTimeInMin());
        }
        practiceExam.setEndTime(practiceExam.getStartTime().plusMinutes(practiceExam.getAllocatedTimeInMin()));
        setExamStatus(practiceExam);

        practiceExam.setExamineeUserName(userUtil.getUserName());
        return practiceExam;
    }

    @Transactional
    public PracticeExam addPracticeExam(PrExamAddRequest addRequest) {
        Exam exam = examService.getExam(addRequest.getParentExamId());
        PracticeExam practiceExam = buildPracticeExam(exam, addRequest);

        try {
            practiceExamRepository.save(practiceExam);
            return practiceExam;
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }

    private ExamResponse buildExamResponse(PracticeExam exam) {
        return ExamResponse.builder()
                .id(exam.getId())
                .name(exam.getName())
                .description(exam.getDescription())
                .startTime(exam.getStartTime())
                .allocatedTimeInMin(exam.getAllocatedTimeInMin())
                .endTime(exam.getEndTime())
                .status(exam.getStatus())
                .totalQuestions(exam.getTotalQuestions())
                .totalMarks(exam.getTotalMarks())
                .build();
    }

    private ExamQuesResponse buildExamQuesResponse(PracticeExam practiceExam, List<QuesResponse> questions) {
        return ExamQuesResponse.builder()
                .examResponse(buildExamResponse(practiceExam))
                .questions(questions)
                .build();
    }

    public ExamQuesResponse getExamQuestions(int id, Pageable pageable) {
        PracticeExam practiceExam = getPracticeExam(id);
        if (userUtil.hasFetchPermission(practiceExam)) {
            throw new AccessDeniedException("You do not have permission to attend the exam!");
        }

        List<QuesResponse> quesResponses = examQuesService.getQuestionList(practiceExam.getParentExamId(), pageable);
        return buildExamQuesResponse(practiceExam, quesResponses);
    }

    public Optional<PracticeExam> findById(int id) {
        return practiceExamRepository.findById(id);
    }

    public PracticeExam getPracticeExam(int id) {
        return findById(id)
                .orElseThrow(() -> new NotFoundException("Practice Exam not found with id " + id));
    }

    public Page<PracticeExam> findPracticeExams(Pageable pageable) {
        return practiceExamRepository.findAll(pageable);
    }

    @Transactional
    public PracticeExam cancelPracticeExam(int id) {
        PracticeExam practiceExam = getPracticeExam(id);
        if (userUtil.hasEditPermission(practiceExam)) {
            throw new AccessDeniedException("You do not have permission to cancel the exam!");
        }

        updateExamStatus(practiceExam);

        if (ExamStatus.ENDED.equals(practiceExam.getStatus())) {
            throw new AccessDeniedException("This exam is already ended!");
        }

        practiceExam.setStatus(ExamStatus.CANCELLED);
        return practiceExam;
    }

    private boolean isReschedulePossible(PracticeExam practiceExam) {
        return ExamStatus.NOT_SCHEDULED.equals(practiceExam.getStatus()) ||
                ExamStatus.NOT_STARTED.equals(practiceExam.getStatus());
    }

    @Transactional
    public PracticeExam reschedulePracticeExam(int id, PrExamEditRequest editRequest) {
        PracticeExam practiceExam = getPracticeExam(id);
        if (userUtil.hasEditPermission(practiceExam)) {
            throw new AccessDeniedException("You do not have permission to reschedule the exam!");
        }
        updateExamStatus(practiceExam);

        if (isReschedulePossible(practiceExam)) {
            practiceExam.setStartTime(editRequest.getStartTime());
            if (editRequest.getAllocatedTimeInMin() != null) {
                practiceExam.setAllocatedTimeInMin(editRequest.getAllocatedTimeInMin());
            }
            practiceExam.setEndTime(practiceExam.getStartTime().plusMinutes(practiceExam.getAllocatedTimeInMin()));
        } else {
            throw new AccessDeniedException("Now reschedule is impossible!");
        }

        updateExamStatus(practiceExam);
        return practiceExam;
    }

    public List<PracticeExam> getRunningExams() {
        return practiceExamRepository.findAllByStatus(ExamStatus.RUNNING);
    }

    private double calculateMarks(int correct, int wrong) {
        return correct - (wrong * 2);
    }

    private void updateMarkAndPosition(PracticeExam practiceExam) {
        SubmissionStatistics statistics =
                submissionService.getStatistics(practiceExam.getId(), practiceExam.getExamineeUserName());
        statistics.setNotAnswered(practiceExam.getTotalQuestions() - statistics.getAnswered());
        practiceExam.setMarksGained(calculateMarks(statistics.getCorrect(), statistics.getWrong()));
    }

    private void endExam(PracticeExam practiceExam, LocalDateTime now) {
        if (practiceExam.getEndTime().isBefore(now)) {
            practiceExam.setStatus(ExamStatus.ENDED);
            updateMarkAndPosition(practiceExam);
        }
    }

    @Transactional
    public void endExam(int id) {
        PracticeExam practiceExam = getPracticeExam(id);
        LocalDateTime now = LocalDateTime.now();
        endExam(practiceExam, now);
    }

    @Transactional
    public void checkExamEndingStatus() {
        List<PracticeExam> runningExams = getRunningExams();
        LocalDateTime now = LocalDateTime.now();
        runningExams
                .forEach(exam -> endExam(exam, now));
    }
}
