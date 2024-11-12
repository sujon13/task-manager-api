package com.example.exam.service;

import com.example.exam.enums.ExamType;
import com.example.exam.model.Exam;
import com.example.exam.model.Result;
import com.example.exam.model.SubmissionStatistics;
import com.example.exam.repository.ResultRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class ResultService {
    private final ResultRepository resultRepository;
    private final SubmissionService submissionService;
    private final UserExamRecordService userExamRecordService;

    @Transactional(readOnly = true)
    Optional<Result> findByExamId(final int examId, String examinee) {
        return resultRepository.findByExamIdAndExaminee(examId, examinee);
    }

    private Result buildResult(Exam exam, String examinee, SubmissionStatistics statistics) {
        Result result = new Result();
        result.setExamId(exam.getId());
        result.setExamType(exam.getExamType());
        result.setExaminee(examinee);
        result.setMarksObtained(calculateMarks(statistics));
        return result;
    }

    public void save(Result result) {
        resultRepository.save(result);
    }

    public void save(List<Result> results) {
        resultRepository.saveAll(results);
    }

    private double calculateMarks(SubmissionStatistics statistics) {
        return statistics.getCorrect() - (statistics.getWrong() * 0.5);
    }

    public void updateMark(Exam exam, String examinee) {
        SubmissionStatistics statistics = submissionService.getStatistics(exam, examinee);

        Result result = buildResult(exam, examinee, statistics);
        save(result);
    }

    private void handlePracticeExam(Exam exam) {
        if (userExamRecordService.hasUserExitedFromTheExam(exam.getId())) {
            log.info("User exited from the exam and mark is already updated");
        } else {
            updateMark(exam, exam.getExaminee());
        }
    }

    private List<Result> getSortedResults(List<Result> results) {
        return results.stream()
                .sorted(Comparator.comparingDouble(Result::getMarksObtained).reversed())
                .toList();
    }

    private List<Result> addMeritPosition(List<Result> results) {
        List<Result> sortedResults = getSortedResults(results);

        sortedResults.getFirst().setPosition(1);

        int numOfExamineesAttended = sortedResults.size();
        int merit = 1;
        for (int pos = 1; pos < numOfExamineesAttended; pos++) {
            Result current = sortedResults.get(pos);
            Result previous = sortedResults.get(pos - 1);

            if (Objects.equals(current.getMarksObtained(), previous.getMarksObtained())) {
                current.setPosition(previous.getPosition());
            } else {
                current.setPosition(merit);
            }

            merit++;
        }
        return sortedResults;
    }

    public void updateMarkAndMeritPosition(Exam exam) {
        if (exam.getExamType().isPractice()) {
            handlePracticeExam(exam);
            return;
        }

        Map<String, SubmissionStatistics> statisticsMap = submissionService.getExamStatistics(exam);
        List<Result> results = statisticsMap.entrySet()
                .stream()
                .map(entry -> buildResult(exam, entry.getKey(), entry.getValue()))
                .toList();

        List<Result> modifiedResults = addMeritPosition(results);
        save(modifiedResults);
    }
}
