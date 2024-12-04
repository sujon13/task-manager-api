package com.example.exam.controller;

import com.example.exam.model.ExamQuesEditRequest;
import com.example.exam.model.ExamQuesRequest;
import com.example.exam.model.ExamQuesResponse;
import com.example.exam.entity.ExamQuestion;
import com.example.exam.service.ExamQuesService;
import com.example.exam.service.ExamQuesValidationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/exams")
public class ExamQuesController {
    private final ExamQuesService examQuesService;
    private final ExamQuesValidationService validationService;

    @GetMapping("/{exam-id}/questions")
    public ExamQuesResponse getExamQuestions(
            @PathVariable("exam-id") final int examId,
            @PageableDefault(page = 0, size = 20, sort = "createdAt", direction = Sort.Direction.ASC) final Pageable pageable) {

        return examQuesService.getExamQuestions(examId, pageable);
    }

    @PostMapping("/{exam-id}/questions")
    public ResponseEntity<?> addExamQuestion(@PathVariable("exam-id") final int examId,
            @Valid @RequestBody final List<ExamQuesRequest> requestList) {

        if (!validationService.canQuesBeAdded(examId, requestList.size())) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body("Increase question limit in the exam section to add new questions");
        }

        try {
            List<ExamQuestion> examQuestionList = examQuesService.createExamQuestions(requestList);
            return ResponseEntity.status(HttpStatus.CREATED).body(examQuestionList);
        } catch (RuntimeException exception) {
            log.error(exception.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @PutMapping("/{exam-id}/questions/{id}")
    public ExamQuestion updateExamQuestion(
            @PathVariable("exam-id") int examId,
            @PathVariable final int id,
            @Valid @RequestBody final ExamQuesEditRequest request) {

        return examQuesService.updateExamQuestion(id, request);
    }

}
