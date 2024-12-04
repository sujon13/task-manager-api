package com.example.qa.controller;

import com.example.qa.model.Comment;
import com.example.qa.model.CommentRequest;
import com.example.qa.service.CommentService;
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
@RequestMapping("/api/v1/comments")
public class CommentController {
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
    public Page<Comment> getCommentsOfQuestion(
            @RequestParam("question_id") final int questionId,
            @PageableDefault(page = 0, size = 10, sort = "likeCount", direction = Sort.Direction.DESC) final Pageable pageable) {

        Page<Comment> commentPage = commentService.findByQuestionId(questionId, pageable);
        log.debug(commentPage.getContent().size() + " comments found for question " + questionId);
        return commentPage;
    }

    @PatchMapping("/like/{id}")
    public ResponseEntity<Comment> likeComment(@PathVariable final int id) {
        return commentService.likeComment(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.CONFLICT).build());
    }
}
