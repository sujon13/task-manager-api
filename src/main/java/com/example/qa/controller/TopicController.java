package com.example.qa.controller;

import com.example.qa.model.Topic;
import com.example.qa.model.TopicRequest;
import com.example.qa.service.TopicService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.web.bind.annotation.*;


@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/topics")
public class TopicController {
    private final TopicService topicService;

    @PostMapping("")
    public Topic addTopic(@Valid @RequestBody final TopicRequest topicRequest) throws BadRequestException {
        return topicService.addTopic(topicRequest);
    }

    @PutMapping("/{id}")
    public Topic editTopic(@PathVariable final int id,
                           @Valid @RequestBody final TopicRequest topicRequest) {
        return topicService.editTopic(id, topicRequest);
    }

    @DeleteMapping("/{id}")
    public void deleteTopic(@PathVariable final int id) {
        topicService.deleteById(id);
    }
}
