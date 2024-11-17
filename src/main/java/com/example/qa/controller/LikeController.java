package com.example.qa.controller;

import com.example.qa.model.Like;
import com.example.qa.model.LikeRequest;
import com.example.qa.service.LikeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/likes")
public class LikeController {
    private final LikeService likeService;

    @GetMapping("")
    public String test() {
        log.info("is virtual: {} id: {}", Thread.currentThread().isVirtual(), Thread.currentThread().threadId());
        return "hello";
    }

    @PostMapping("")
    public ResponseEntity<Like> addLike(@Valid @RequestBody final LikeRequest likeRequest) {
        if (likeService.alreadyLikedByThisUserAndParent(likeRequest)) {
            log.error("{} is already liked", likeRequest.getParentId());
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }

        try {
            Like like = likeService.createLike(likeRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body(like);
        } catch (RuntimeException exception) {
            log.error(exception.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
}
