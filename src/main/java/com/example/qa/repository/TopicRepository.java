package com.example.qa.repository;

import com.example.qa.model.Topic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TopicRepository extends JpaRepository<Topic, Integer> {
    List<Topic>findAllByParentId(int parentId);

    boolean existsByParentId(int parentId);
}