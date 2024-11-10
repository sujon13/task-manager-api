package com.example.exam.service;

import com.example.UserUtil;
import com.example.exam.model.*;
import com.example.exam.repository.SubmissionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SubmissionService {
    private final SubmissionRepository submissionRepository;
    private final UserUtil userUtil;
    private final ExamQuesService examQuesService;
    private final UserExamRecordService userExamRecordService;
    private final ExamStatusService examStatusService;


    boolean isAnsCorrect(SubmissionRequest request) {
        Optional<ExamQuestion> optionalExamQuestion =
                examQuesService.findByExamIdAndQuestionId(request.getExamId(), request.getQuestionId());
        if (optionalExamQuestion.isEmpty())
            return false;
        ExamQuestion examQuestion = optionalExamQuestion.get();

        return request.getGivenAns().equals(examQuestion.getAns());
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

    private void checkSecurity(final int examId) {
        boolean canAnsBeSubmittedNow =
                examStatusService.isExamRunning(examId) && userExamRecordService.hasUserEnteredTheExam(examId);
        if (!canAnsBeSubmittedNow) {
            throw new AccessDeniedException("Exam is ended or you have already exited from the exam!");
        }
    }

    @Transactional
    public Submission addSubmission(SubmissionRequest request) {
        checkSecurity(request.getExamId());

        Submission submission = buildSubmission(request);
        submissionRepository.save(submission);
        return submission;
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
        List<Submission> submissionList = submissionRepository.findAllByExamIdAndExaminee(exam.getId(), examinee);
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
