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
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
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
    private final SubmissionService submissionService;
    private final UserUtil userUtil;
    private final UserExamRecordService userExamRecordService;
    private final ResultService resultService;

    private void addExamTimeAndStatus(Exam exam, ExamAddRequest request) {
        exam.setStartTime(request.getStartTime());
        exam.setAllocatedTimeInMin(request.getAllocatedTimeInMin());
        if (exam.getStartTime() != null) {
            exam.setEndTime(exam.getStartTime().plusMinutes(exam.getAllocatedTimeInMin()));
            examStatusService.setExamStatus(exam);
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
        return saveExam(exam);
    }

    private Exam cloneExam(Exam parentExam, ExamCloneRequest cloneRequest) {
        Exam exam = new Exam();
        BeanUtils.copyProperties(parentExam, exam, "id");

        exam.setParentId(parentExam.getId());

        exam.setStartTime(cloneRequest.getStartTime());
        exam.setAllocatedTimeInMin(cloneRequest.getAllocatedTimeInMin());
        exam.setEndTime(exam.getStartTime().plusMinutes(exam.getAllocatedTimeInMin()));
        exam.setExamType(cloneRequest.getExamType());

        examStatusService.setExamStatus(exam);

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
        Exam parentExam = getExam(parentId);

        checkExamTypeValidity(parentExam.getExamType(), cloneRequest.getExamType());
        checkClonedExamSecurity(cloneRequest.getExamType());

        Exam clonedExam = cloneExam(parentExam, cloneRequest);
        saveExam(clonedExam);

        cloneExamQuestions(clonedExam.getParentId(), clonedExam.getId());
        return clonedExam;
    }

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

    public Page<Exam> findExams(Pageable pageable) {
        return examRepository.findAll(pageable);
    }

    public List<Exam> getRunningExams() {
        return examRepository.findAllByStatus(ExamStatus.RUNNING);
    }

    private double calculateMarks(SubmissionStatistics statistics) {
        return statistics.getCorrect() - (statistics.getWrong() * 0.5);
    }

    private ResultRequest buildResultRequest(Exam exam,  String examinee, SubmissionStatistics statistics) {
        return ResultRequest.builder()
                .examId(exam.getId())
                .examType(exam.getExamType())
                .examinee(examinee)
                .marksObtained(calculateMarks(statistics))
                .build();
    }

    private void updateMarks(Exam exam, String examinee) {
        SubmissionStatistics statistics = submissionService.getStatistics(exam, examinee);

        ResultRequest resultRequest = buildResultRequest(exam, examinee, statistics);
        resultService.addResults(List.of(resultRequest));
    }

    private void updateMarks(Exam exam) {
        Map<String, SubmissionStatistics> statisticsMap = submissionService.getExamStatistics(exam);
        List<ResultRequest> resultRequests = statisticsMap.entrySet()
                .stream()
                .map(entry -> buildResultRequest(exam, entry.getKey(), entry.getValue()))
                .toList();
        resultService.addResults(resultRequests);
    }

    private void endExam(Exam exam, LocalDateTime now) {
        if (exam.getEndTime().isBefore(now)) {
            exam.setStatus(ExamStatus.ENDED);
            updateMarks(exam);
        }
    }

    @Transactional
    public void checkExamEndingStatus() {
        List<Exam> runningExams = getRunningExams();
        LocalDateTime now = LocalDateTime.now();
        runningExams
                .forEach(exam -> endExam(exam, now));
    }

    @Transactional
    public void enterExam(final int examId) {
        Exam exam = getExam(examId);

        if (!examStatusService.isExamRunning(exam)) {
            throw new AccessDeniedException("You can enter only in running exams");
        }

        if (!ExamType.isLiveOrPractice(exam.getExamType())) {
            throw new AccessDeniedException("You can enter only in live or practice exams");
        }

        if (ExamType.PRACTICE.equals(exam.getExamType()) && !userUtil.hasEditPermission(exam)) {
            throw new AccessDeniedException("You do not have permission to enter this exam");
        }

        userExamRecordService.enter(examId);
    }

    @Transactional
    public void exitFromExam(final int examId) {
        Exam exam = getExam(examId);

        if (!examStatusService.isExamRunning(exam)) {
            throw new AccessDeniedException("Exit is only possible from a running exam!");
        }

        if (!ExamType.isLiveOrPractice(exam.getExamType())) {
            throw new AccessDeniedException("Exit is only possible from a live or practice exam");
        }

        if (ExamType.PRACTICE.equals(exam.getExamType()) && !userUtil.hasEditPermission(exam)) {
            throw new AccessDeniedException("You do not have permission to exit from this exam");
        }

        userExamRecordService.exit(exam);
        if (ExamType.PRACTICE.equals(exam.getExamType()))
            updateMarks(exam, userUtil.getUserName());
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