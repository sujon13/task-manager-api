package com.example.exam.controller;

import com.example.exam.model.Exam;
import com.example.exam.model.ExamEditRequest;
import com.example.exam.model.ExamQuesResponse;
import com.example.exam.model.ExamRequest;
import com.example.exam.service.ExamService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/exams")
public class ExamRestController {
    private final ExamService examService;

    @PostMapping("")
    public ResponseEntity<Exam> addExam(@Valid @RequestBody final ExamRequest request) {
        try {
            Exam exam = examService.createExam(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(exam);
        } catch (RuntimeException exception) {
            log.error(exception.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("")
    public Page<Exam> getExams(
            @PageableDefault(page = 0, size = 10, sort = "createdAt", direction = Sort.Direction.DESC) final Pageable pageable) {

        return examService.findExams(pageable);
    }

    @PutMapping("/{id}")
    public Exam updateExam(@PathVariable final int id, @Valid @RequestBody final ExamEditRequest request) {
        return examService.updateExam(id, request);
    }

}
