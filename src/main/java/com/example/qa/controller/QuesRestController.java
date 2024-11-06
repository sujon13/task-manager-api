package com.example.qa.controller;

import com.example.qa.model.Question;
import com.example.qa.model.QuestionEditRequest;
import com.example.qa.model.QuestionRequest;
import com.example.qa.service.QuestionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/questions")
public class QuesRestController {
    private final QuestionService questionService;

    @PostMapping("")
    public Question createQuestion(@Valid @RequestBody QuestionRequest request) {
        return questionService.creteQuestion(request);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Question> getQuestionById(@PathVariable final int id) {
        return questionService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
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
