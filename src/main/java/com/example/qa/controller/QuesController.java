package com.example.qa.controller;

import com.example.qa.model.QuesResponse;
import com.example.qa.model.Question;
import com.example.qa.model.QuestionEditRequest;
import com.example.qa.model.QuestionRequest;
import com.example.qa.service.QuestionService;
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
@RequestMapping("/api/v1/questions")
public class QuesController {
    private final QuestionService questionService;

    @PostMapping("")
    public Question createQuestion(@Valid @RequestBody QuestionRequest request) {
        return questionService.createQuestion(request);
    }

    @GetMapping("/{id}")
    public QuesResponse getQuesResponseById(@PathVariable final int id) {
        return questionService.getQuesResponseById(id);
    }

    @GetMapping("")
    public Page<QuesResponse> getQuesResponses(
            @RequestParam final int topicId,
            @PageableDefault(page = 0, size = 10, sort = "createdAt", direction = Sort.Direction.DESC) final Pageable pageable
    ) {
        return questionService.getQuesResponsesByTopicId(topicId, pageable);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Question> updateQuestion(
            @PathVariable final int id, @Valid @RequestBody QuestionEditRequest request) {

        return ResponseEntity.ok(questionService.editQuestion(id, request));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Question> likeQuestion(@PathVariable final int id) {
        return questionService.likeQuestion(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.CONFLICT).build());
    }
}
