package com.example.exam.controller;

import com.example.exam.entity.Post;
import com.example.exam.model.PostRequest;
import com.example.exam.service.PostService;
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
@RequestMapping("/api/v1/posts")
public class PostController {
    private final PostService postService;

    @PostMapping("")
    public ResponseEntity<Post> addPost(@Valid @RequestBody final PostRequest request) {
        try {
            Post post = postService.addPost(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(post);
        } catch (RuntimeException exception) {
            log.error(exception.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("")
    public Page<Post> getAllPosts(
        @PageableDefault(page = 0, size = 10, sort = "createdAt", direction = Sort.Direction.DESC) final Pageable pageable)
    {
        return postService.findAllPosts(pageable);
    }

    @GetMapping("/{id}")
    public Post getPostById(@PathVariable final Integer id) {
        return postService.getPost(id);
    }

    @PutMapping("/{id}")
    public Post updatePost(@PathVariable final int id, @Valid @RequestBody final PostRequest request) {
        return postService.editPost(id, request);
    }
}
