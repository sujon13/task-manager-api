package com.example.qa.repository;

import com.example.qa.model.Option;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OptionRepository extends JpaRepository<Option, Integer> {
    List<Option> findAllByQuestionId(int questionId);
    List<Option> findAllByQuestionIdIn(List<Integer> questionIds);

    void deleteAllByQuestionId(int questionId);
}
