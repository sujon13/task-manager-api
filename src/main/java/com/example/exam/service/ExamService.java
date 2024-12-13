package com.example.exam.service;

import com.example.util.UserUtil;
import com.example.exam.entity.ExamTaker;
import com.example.exam.entity.Post;
import com.example.exam.enums.ExamType;
import com.example.exam.entity.Exam;
import com.example.exam.model.ExamResponse;
import com.example.exam.repository.ExamRepository;
import com.example.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;


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
    private final PostService postService;
    private final ExamTakerService examTakerService;

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

    public List<Exam> findLiveAndPracticeExams() {
        return examRepository.findAllByExamTypeIn(List.of(ExamType.LIVE, ExamType.PRACTICE));
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

    public ExamResponse buildExamResponse(final Exam exam, final Post post, final ExamTaker examTaker) {
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
                .post(post)
                .examTaker(examTaker)
                .build();
    }

    public ExamResponse buildExamResponse(final Exam exam) {
        final Post post = postService.findById(exam.getPostId()).orElse(null);
        final ExamTaker examTaker = examTakerService.findById(exam.getExamTakerId()).orElse(null);
        return buildExamResponse(exam, post, examTaker);
    }

    private Map<Integer, Post> buildIdToPostMap(final List<Exam> exams) {
        Set<Integer> postIdList = exams.stream()
                .map(Exam::getPostId)
                .filter(Objects::nonNull)
                .collect(Collectors.toUnmodifiableSet());
        return postService
                .findAllByIds(postIdList)
                .stream()
                .collect(Collectors.toMap(Post::getId, Function.identity()));
    }

    private Map<Integer, ExamTaker> getIdToExamTakerMap(final List<Exam> exams) {
        Set<Integer> examTakerIdList = exams
                .stream()
                .map(Exam::getExamTakerId)
                .filter(Objects::nonNull)
                .collect(Collectors.toUnmodifiableSet());
        return examTakerService
                .findAllByIds(examTakerIdList)
                .stream()
                .collect(Collectors.toMap(ExamTaker::getId, Function.identity()));

    }

    private List<ExamResponse> buildExamResponseList(final List<Exam> exams) {
        Map<Integer, Post> idToPostMap = buildIdToPostMap(exams);
        Map<Integer, ExamTaker> idToExamTakerMap = getIdToExamTakerMap(exams);
        return exams.stream()
                .map(exam -> buildExamResponse(
                        exam,
                        exam.getPostId() != null ? idToPostMap.get(exam.getPostId()) : null,
                        exam.getExamTakerId() != null ?  idToExamTakerMap.get(exam.getExamTakerId()) : null)
                )
                .toList();

    }

    public Page<ExamResponse> findExams(ExamType examType, Pageable pageable) {
        Page<Exam> examPage = examType.isPractice()
                ? examRepository.findAllByExamTypeAndExaminee(examType, userUtil.getUserName(), pageable)
                : examRepository.findAllByExamType(examType, pageable);

        List<ExamResponse> examResponseList = buildExamResponseList(examPage.getContent());
        return new PageImpl<>(examResponseList, pageable, examPage.getTotalElements());
    }

    public ExamResponse findExam(final int examId) {
        Exam exam = getExam(examId);
        return buildExamResponse(exam);
    }

}