package com.example.exam.service;

import com.example.util.UserUtil;
import com.example.exam.entity.Exam;
import com.example.exam.entity.ExamQuestion;
import com.example.exam.entity.Submission;
import com.example.exam.model.ExamQuesEditRequest;
import com.example.exam.model.ExamQuesRequest;
import com.example.exam.model.ExamQuesResponse;
import com.example.exam.model.ExamResponse;
import com.example.exam.repository.ExamQuesRepository;
import com.example.exception.NotFoundException;
import com.example.qa.model.QuesResponse;
import com.example.qa.model.Question;
import com.example.qa.service.QuestionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
public class ExamQuesService {
    private final ExamQuesRepository examQuesRepository;
    private final ExamService examService;
    private final QuestionService questionService;
    private final ExamStatusService examStatusService;
    private final UserExamRecordService userExamRecordService;
    private final SubmissionService submissionService;
    private final ResultService resultService;
    private final UserUtil userUtil;

    private ExamQuestion buildExamQuestion(ExamQuesRequest request, Map<Integer, Integer> quesIdToAnsMap) {
        ExamQuestion examQuestion = new ExamQuestion();
        examQuestion.setQuestionId(request.getQuestionId());
        examQuestion.setExamId(request.getExamId());
        if (request.getMarks() != null)
            examQuestion.setMarks(request.getMarks());
        if (request.getAns() != null) {
            examQuestion.setAns(request.getAns());
        } else {
            examQuestion.setAns(quesIdToAnsMap.get(request.getQuestionId()));
        }
        return examQuestion;
    }

    private Map<Integer, Integer> getQuestionIdToAnsMap(List<ExamQuesRequest> examQuesRequests) {
        List<Integer> questionIds = examQuesRequests.stream()
                .map(ExamQuesRequest::getQuestionId)
                .toList();
        return questionService.findAllByIds(questionIds)
                .stream()
                .collect(Collectors.toMap(Question::getId, Question::getMcqAns));
    }

    @Transactional
    public List<ExamQuestion> createExamQuestions(List<ExamQuesRequest> examQuesRequests) {
        Map<Integer, Integer> quesIdToAnsMap = getQuestionIdToAnsMap(examQuesRequests);
        List<ExamQuestion> examQuestions = examQuesRequests.stream()
                .map(request -> buildExamQuestion(request, quesIdToAnsMap))
                .toList();

        try {
            examQuesRepository.saveAll(examQuestions);
            return examQuestions;
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }

    private void editExamQuestion(ExamQuestion examQuestion, ExamQuesEditRequest request) {
        if (request.getMarks() != null)
            examQuestion.setMarks(request.getMarks());
        if (request.getAns() != null)
            examQuestion.setAns(request.getAns());
    }

    @Transactional
    public ExamQuestion updateExamQuestion(final int id, ExamQuesEditRequest request) {
        ExamQuestion examQuestion = getExamQuestion(id);

        if (!userUtil.hasEditPermission(examQuestion)) {
            throw new AccessDeniedException("You do not have permission to edit this exam question");
        }

        editExamQuestion(examQuestion, request);
        return examQuestion;
    }

    @Transactional
    public void saveExamQuestions(List<ExamQuestion> examQuestions) {
        examQuesRepository.saveAll(examQuestions);
    }

    public Optional<ExamQuestion> findById(final int id) {
        return examQuesRepository.findById(id);
    }

    public ExamQuestion getExamQuestion(final int id) {
        return findById(id)
                .orElseThrow(() -> new NotFoundException("Exam question not found with id " + id));
    }

    public Page<ExamQuestion> findAllByExamId(final int examId, Pageable pageable) {
        return examQuesRepository.findAllByExamId(examId, pageable);
    }

    public List<ExamQuestion> findAllByExamId(final int examId) {
        return examQuesRepository.findAllByExamId(examId);
    }

    public Optional<ExamQuestion> findByExamIdAndQuestionId(final int examId, final int questionId) {
        return examQuesRepository.findByExamIdAndQuestionId(examId, questionId);
    }

    public boolean isQuesAlreadyChosenForThisExam(ExamQuestion request) {
        return examQuesRepository.existsByQuestionIdAndExamId(request.getQuestionId(), request.getExamId());
    }


    public int getCurrentQuesCount(final int examId) {
        return examQuesRepository.countByExamId(examId);
    }

    private void addResult(ExamResponse examResponse, String examinee) {
        resultService.findByExamId(examResponse.getId(), examinee).
                ifPresent(result -> {
                    examResponse.setMarksObtained(result.getMarksObtained());
                    examResponse.setPosition(result.getPosition());
                });
    }

    private ExamQuesResponse buildExamQuesResponse(Exam exam, List<QuesResponse> questions) {
        return ExamQuesResponse.builder()
                .examResponse(examService.buildExamResponse(exam))
                .questions(questions)
                .build();
    }

    private List<QuesResponse> getQuestionList(final int examId, Pageable pageable) {
        Page<ExamQuestion> examQuestions = findAllByExamId(examId, pageable);

        List<Integer> quesIds = examQuestions.stream()
                .map(ExamQuestion::getQuestionId)
                .toList();
        return questionService.getQuesResponsesByIds(quesIds);
    }

    private Map<Integer, Integer> getQuesIdToSubmittedAndMap(final int examId, final String examinee) {
        List<Submission> submissionList = submissionService.findSubmissionsBy(examId, examinee);
        return submissionList.stream()
                .collect(Collectors.toMap(Submission::getQuesId, Submission::getGivenAns));
    }

    private void addSubmittedAnsToQuestionsByUser(final int examId, final String examinee, List<QuesResponse> quesResponses) {
        Map<Integer, Integer> quesIdToGivenAnsMap = getQuesIdToSubmittedAndMap(examId, examinee);
        quesResponses
                .forEach(quesResponse -> quesResponse.setGivenAns(quesIdToGivenAnsMap.get(quesResponse.getId())));
    }

    private void checkExamQuesViewPermission(final Exam exam) {
        if (!exam.getExamType().isLiveOrPractice())
            return;

        if (examStatusService.isExamRunning(exam)) {
            if (!userUtil.hasFetchPermission(exam) && !userExamRecordService.hasUserEnteredTheExam(exam.getId())) {
                throw new AccessDeniedException("You do not have permission to view this exam question");
            }
        } else if (examStatusService.hasExamNotStarted(exam)) {
            throw new AccessDeniedException("This exam has not started yet");
        }
    }

    private void hideAnsDuringExam(List<QuesResponse> quesResponses) {
        quesResponses.forEach(quesResponse -> {
            quesResponse.setExplanation(null);
            quesResponse.setMcqAns(null);
        });
    }

    public ExamQuesResponse getExamQuestions(final int examId, Pageable pageable) {
        final Exam exam = examService.getExam(examId);

        checkExamQuesViewPermission(exam);

        final String examinee = userUtil.getUserName();
        final boolean isUser = userUtil.isUser();

        List<QuesResponse> quesResponses = getQuestionList(examId, pageable);
        if (examStatusService.isExamRunning(exam)) {
            hideAnsDuringExam(quesResponses);
            addSubmittedAnsToQuestionsByUser(examId, examinee, quesResponses);
        } else if (examStatusService.isExamOver(exam) && isUser) {
            addSubmittedAnsToQuestionsByUser(examId, examinee, quesResponses);
        }

        ExamQuesResponse examQuesResponse = buildExamQuesResponse(exam, quesResponses);
        if (examStatusService.isExamOver(exam) && isUser) {
            addResult(examQuesResponse.getExamResponse(), examinee);
        }

        return examQuesResponse;
    }

    public List<QuesResponse> getExamQuestions(final int examId, final Integer topicId) {
        List<ExamQuestion> examQuestionList = findAllByExamId(examId);
        List<Integer> questionIds = examQuestionList.stream()
                .map(ExamQuestion::getQuestionId)
                .toList();
        return questionService.getQuesResponsesByIds(questionIds)
                .stream()
                .filter(quesResponse -> quesResponse.getTopic() != null)
                .filter(quesResponse -> topicId.equals(quesResponse.getTopic().getId()))
                .toList();
    }

    public void makeExamQuestionsVisible(final int examId) {
        List<Integer> examQuestionIds = findAllByExamId(examId)
                .stream()
                .map(ExamQuestion::getQuestionId)
                .toList();

        questionService.makeQuestionsVisible(examQuestionIds);
    }

}
