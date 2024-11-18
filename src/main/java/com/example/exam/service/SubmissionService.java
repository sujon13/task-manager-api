package com.example.exam.service;

import com.example.UserUtil;
import com.example.exam.model.Exam;
import com.example.exam.model.Submission;
import com.example.exam.model.SubmissionRequest;
import com.example.exam.model.SubmissionStatistics;
import com.example.exam.repository.ExamQuesRepository;
import com.example.exam.repository.SubmissionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SubmissionService {
    private final SubmissionRepository submissionRepository;
    private final UserUtil userUtil;
    private final ExamQuesRepository examQuesRepository;
    private final UserExamRecordService userExamRecordService;
    private final ExamStatusService examStatusService;


    private boolean isAnsCorrect(SubmissionRequest request) {
        return examQuesRepository.findByExamIdAndQuestionId(request.getExamId(), request.getQuestionId())
                .map(examQuestion -> request.getGivenAns().equals(examQuestion.getAns()))
                .orElse(false);
    }

    private Submission buildSubmission(SubmissionRequest request) {
        Submission submission = new Submission();
        submission.setExamId(request.getExamId());
        submission.setQuesId(request.getQuestionId());
        submission.setExamType(request.getExamType());
        submission.setExaminee(userUtil.getUserName());
        submission.setGivenAns(request.getGivenAns());
        submission.setCorrect(isAnsCorrect(request));
        return submission;
    }

    private boolean alreadySubmitted(SubmissionRequest request) {
        return submissionRepository.existsByExamIdAndQuesIdAndExaminee(request.getExamId(),
                request.getQuestionId(), userUtil.getUserName());
    }

    private void checkSecurity(final SubmissionRequest request) {
        final int examId = request.getExamId();
        boolean canBeSubmitted =
                examStatusService.isExamRunning(examId) && userExamRecordService.hasUserEnteredTheExam(examId);
        if (!canBeSubmitted) {
            throw new AccessDeniedException("Exam is ended or you have already exited from the exam!");
        }

        if (alreadySubmitted(request)) {
            throw new AccessDeniedException("Ans is already submitted for this question!");
        }
    }

    @Transactional
    public Submission addSubmission(final int examId, SubmissionRequest request) {
        checkSecurity(request);

        Submission submission = buildSubmission(request);
        submissionRepository.save(submission);
        return submission;
    }

    public List<Submission> findSubmissionsBy(final int examId, final String examinee) {
        return submissionRepository.findAllByExamIdAndExaminee(examId, examinee);
    }

    private SubmissionStatistics getStatistics(Exam exam, List<Submission> submissionList) {
        int answered = submissionList.size();
        int correct = (int)submissionList.stream()
                .map(Submission::isCorrect)
                .count();
        return SubmissionStatistics.builder()
                .answered(answered)
                .notAnswered(exam.getTotalQuestions() - answered)
                .correct(correct)
                .wrong(answered - correct)
                .build();
    }

    public SubmissionStatistics getStatistics(Exam exam, String examinee) {
        List<Submission> submissionList = findSubmissionsBy(exam.getId(), examinee);
        return getStatistics(exam, submissionList);
    }

    public Map<String, SubmissionStatistics> getExamStatistics(Exam exam) {
        return submissionRepository.findAllByExamId(exam.getId())
                .stream()
                .collect(Collectors.groupingBy(Submission::getExaminee))
                .entrySet()
                .stream()
                .collect(
                        Collectors.toMap(
                                Map.Entry::getKey,
                                entry -> getStatistics(exam, entry.getValue()))
                );
    }
}
