package com.example.exam.service;

import com.example.UserUtil;
import com.example.exam.model.ExamQuestion;
import com.example.exam.model.SubmissionStatistics;
import com.example.exam.model.Submission;
import com.example.exam.model.SubmissionRequest;
import com.example.exam.repository.SubmissionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SubmissionService {
    private final SubmissionRepository submissionRepository;
    private final UserUtil userUtil;
    private final ExamQuesService examQuesService;

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
        submission.setExamineeUserName(userUtil.getUserName());
        submission.setGivenAns(request.getGivenAns());
        submission.setCorrect(isAnsCorrect(request));
        return submission;
    }

    @Transactional
    public Submission addSubmission(SubmissionRequest request) {
        Submission submission = buildSubmission(request);
        submissionRepository.save(submission);
        return submission;
    }

    public SubmissionStatistics getStatistics(int examId, String userName) {
        List<Submission> submissionList = submissionRepository.findAllByExamIdAndExamineeUserName(examId, userName);
        int answered = submissionList.size();
        int correct = (int)submissionList.stream()
                .map(Submission::isCorrect)
                .count();
        return SubmissionStatistics.builder()
                .answered(answered)
                .correct(correct)
                .wrong(answered - correct)
                .build();
    }

}
