package com.example.qa.controller;

import com.example.qa.model.Comment;
import com.example.qa.model.CommentRequest;
import com.example.qa.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/comments")
public class CommentRestController {
    private final CommentService commentService;

    @PostMapping("")
    public ResponseEntity<Comment> addComment(@Valid @RequestBody final CommentRequest request) {
        try {
            Comment comment = commentService.createComment(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(comment);
        } catch (RuntimeException exception) {
            log.error(exception.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("")
    public ResponseEntity<List<Comment>> getCommentsOfQuestion(@RequestParam("question_id") final int questionId) {
        List<Comment> commentList = commentService.findByQuestionId(questionId);
        log.debug(commentList.size() + " comments found for question " + questionId);
        return ResponseEntity.ok(commentList);
    }
}
