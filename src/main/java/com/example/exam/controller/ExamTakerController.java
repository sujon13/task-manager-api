package com.example.exam.controller;

import com.example.exam.entity.ExamTaker;
import com.example.exam.model.ExamTakerRequest;
import com.example.exam.service.ExamTakerService;
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

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/exam-takers")
public class ExamTakerController {
    private final ExamTakerService ExamTakerService;

    @PostMapping("")
    public ResponseEntity<ExamTaker> addExamTaker(@Valid @RequestBody final ExamTakerRequest request) {
        try {
            ExamTaker ExamTaker = ExamTakerService.addExamTaker(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(ExamTaker);
        } catch (RuntimeException exception) {
            log.error(exception.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("")
    public Page<ExamTaker> getAllExamTakers(
            @PageableDefault(page = 0, size = 10, sort = "createdAt", direction = Sort.Direction.DESC) final Pageable pageable)
    {
        return ExamTakerService.findAll(pageable);
    }

    @GetMapping("/{id}")
    public ExamTaker getExamTakerById(@PathVariable final Integer id) {
        return ExamTakerService.getExamTaker(id);
    }

    @PutMapping("/{id}")
    public ExamTaker updateExamTaker(@PathVariable final int id, @Valid @RequestBody final ExamTakerRequest request) {
        return ExamTakerService.editExamTaker(id, request);
    }
}
