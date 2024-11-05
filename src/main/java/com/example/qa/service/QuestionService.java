package com.example.qa.service;

import com.example.qa.model.Question;
import com.example.qa.model.QuestionEditRequest;
import com.example.qa.model.QuestionRequest;
import com.example.qa.repository.QuestionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class QuestionService {
    private final QuestionRepository questionRepository;
    private final OptionService optionService;


    private Question buildQuestion(QuestionRequest request) {
        Question question = new Question();
        question.setType(request.getType());
        question.setQuestionerId(1); //  need to update
        question.setVersion(request.getVersion());
        question.setQuestionEn(request.getQuestionEn());
        question.setQuestionBn(request.getQuestionBn());
        question.setMcqAns(request.getMcqAns()); // 1 (a) 2 (b) (1-5)
        question.setVisible(request.isVisible());
        return question;
    }

    @Transactional
    public Question creteQuestion(QuestionRequest request) {
        Question question = buildQuestion(request);
        try {
            questionRepository.save(question);
            optionService.createOptions(request.getOptionsRequests(), question.getId());
            return question;
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }

    public Optional<Question> findById(int id) {
        return questionRepository.findById(id);
    }

    public List<Question> findAllById(List<Integer> ids) {
        return questionRepository.findAllById(ids);
    }

    private void editQuestion(Question question, QuestionEditRequest request) {
        if (request.getType() != null)
            question.setType(request.getType());
        if (request.getVersion() != null)
            question.setVersion(request.getVersion());
        if (request.getQuestionEn() != null)
            question.setQuestionEn(request.getQuestionEn());
        if (request.getQuestionBn() != null)
            question.setQuestionBn(request.getQuestionBn());
        if (request.getMcqAns() != null)
            question.setMcqAns(request.getMcqAns());
        if (request.getVisible() != null)
            question.setVisible(request.getVisible());
    }

    @Transactional
    public Question editQuestion(int id, QuestionEditRequest request) {
        Optional<Question> optionalQuestion = questionRepository.findById(id);
        if (optionalQuestion.isEmpty()) {
            throw new RuntimeException("question not found");
        }

        Question question = optionalQuestion.get();
        optionService.editOptions(request.getOptionsRequests(), question.getId());
        editQuestion(question, request);
        return question;
    }

    @Transactional
    public void deleteQuestion(int id) {
        optionService.deleteByQuestionId(id);
        questionRepository.deleteById(id);
    }
}
