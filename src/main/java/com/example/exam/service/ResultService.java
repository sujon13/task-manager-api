package com.example.exam.service;

import com.example.exam.model.Result;
import com.example.exam.model.ResultRequest;
import com.example.exam.repository.ResultRepository;
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
public class ResultService {
    private final ResultRepository resultRepository;
    private

    Optional<Result> findByExamId(final int examId, String examinee) {
        return resultRepository.findByExamIdAndExaminee(examId, examinee);
    }

    private Result buildResult(ResultRequest request) {
        Result result = new Result();
        result.setExamId(request.getExamId());
        result.setExamType(request.getExamType());
        result.setExaminee(request.getExaminee());
        result.setMarksObtained(request.getMarksObtained());
        return result;
    }

    @Transactional
    public void addResults(List<ResultRequest> requests) {
        List<Result> results = requests.stream()
                .map(this::buildResult)
                .toList();
        resultRepository.saveAll(results);
    }

}
