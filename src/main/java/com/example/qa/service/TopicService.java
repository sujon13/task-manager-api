package com.example.qa.service;

import com.example.exception.NotFoundException;
import com.example.qa.model.Topic;
import com.example.qa.model.TopicRequest;
import com.example.qa.repository.TopicRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;


@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TopicService {
    private final TopicRepository topicRepository;

    private Topic buildTopic(TopicRequest request) {
        Topic topic = new Topic();
        topic.setParentId(request.getParentId());
        topic.setEngName(request.getEngName());
        topic.setBngName(request.getBngName());
        return topic;
    }

    private void checkRequestParam(TopicRequest request) throws BadRequestException {
        if (request.getEngName() == null && request.getBngName() == null) {
            throw new BadRequestException("Both english name and bangla name cannot be null");
        }
    }

    @Transactional
    public Topic addTopic(TopicRequest request) throws BadRequestException {
        checkRequestParam(request);
        Topic topic = buildTopic(request);
        return topicRepository.save(topic);
    }

    private void editTopic(Topic topic, TopicRequest request) {
        if (request.getParentId() != null) {
            topic.setParentId(request.getParentId());
        }
        if (request.getEngName() != null) {
            topic.setEngName(request.getEngName());
        }
        if (request.getBngName() != null) {
            topic.setBngName(request.getBngName());
        }
    }

    @Transactional
    public Topic editTopic(final int id, final TopicRequest request) {
        Topic topic = getTopic(id);
        editTopic(topic, request);
        return topic;
    }

    public Optional<Topic> findById(final int id) {
        return topicRepository.findById(id);
    }

    public List<Topic> findAllByIds(Collection<Integer> ids) {
        return topicRepository.findAllById(ids);
    }

    public List<Topic> findAllSubTopics(final int id) {
        List<Topic> subTopicList = new ArrayList<>();

        List<Topic> directChildTopics = findByParentId(id);
        directChildTopics.forEach(child -> subTopicList.addAll(findAllSubTopics(child.getId())));

        Optional<Topic> thisTopic = findById(id);
        thisTopic.ifPresent(subTopicList::add);

        return subTopicList;
    }

    public List<Integer> findAllSubTopicsIds(final int id) {
        return findAllSubTopics(id)
                .stream()
                .map(Topic::getId)
                .toList();
    }

    public Topic getTopic(final int id) {
        return findById(id)
                .orElseThrow(() -> new NotFoundException("Topic not found with id " + id));
    }

    public List<Topic> findByParentId(int parentId) {
        return topicRepository.findAllByParentId(parentId);
    }

    @Transactional
    public void deleteById(final int id) {
        if (topicRepository.existsByParentId(id)) {
            throw new AccessDeniedException("There is sub topic under this topic");
        }
        topicRepository.deleteById(id);
    }

}
