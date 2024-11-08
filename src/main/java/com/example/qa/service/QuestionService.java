package com.example.qa.service;

import com.example.UserUtil;
import com.example.exception.NotFoundException;
import com.example.qa.enums.QuesTypeEnum;
import com.example.qa.enums.TypeEnum;
import com.example.qa.model.*;
import com.example.qa.repository.QuestionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
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
    private final LikeService likeService;
    private final UserUtil userUtil;


    private Question buildQuestion(QuestionRequest request) {
        Question question = new Question();
        question.setParentId(request.getParentId());
        question.setSerial(request.getSerial());
        question.setQuesType(request.getQuesType());
        question.setQuestionerUserName(userUtil.getUserName()); //  need to update
        question.setVersion(request.getVersion());
        question.setQuestionEn(request.getQuestionEn());
        question.setQuestionBn(request.getQuestionBn());
        question.setMcqAns(request.getMcqAns()); // 1 (a) 2 (b) (1-5)
        question.setVisible(request.isVisible());
        return question;
    }

    private void createSubQuestions(Integer parentId, List<QuestionRequest> subQuestionRequests) {
        subQuestionRequests
                .forEach(subQuesRequest -> {
                    subQuesRequest.setParentId(parentId);
                    creteQuestion(subQuesRequest);
                });
    }

    @Transactional
    public Question creteQuestion(QuestionRequest request) {
        Question question = buildQuestion(request);
        try {
            questionRepository.save(question);
            if (QuesTypeEnum.MCQ.equals(request.getQuesType())) {
                optionService.createOptions(request.getOptionRequests(), question.getId());
            } else {
                createSubQuestions(question.getId(), request.getSubQuesRequests());
            }
            return question;
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }

    public Optional<Question> findById(int id) {
        return questionRepository.findById(id);
    }

    private QuesResponse createResponse(Question question) {
        QuesResponse quesResponse = new QuesResponse();
        BeanUtils.copyProperties(question, quesResponse);

        if (QuesTypeEnum.MCQ.equals(question.getQuesType())) {
            quesResponse.setOptionResponses(optionService.getOptionResponsesByQuestionId(quesResponse.getId()));
        } else {
            List<QuesResponse> subQuesResponses = questionRepository.findAllByParentId(question.getId())
                    .stream()
                    .map(this::createResponse)
                    .toList();
            quesResponse.setSubResponses(subQuesResponses);
        }
        return quesResponse;
    }

    public QuesResponse getQuesResponseById(int id) {
        Optional<Question> optionalQuestion = questionRepository.findById(id);
        if (optionalQuestion.isEmpty()) {
            throw new NotFoundException("Question not found with id " + id);
        }
        Question question = optionalQuestion.get();

        return createResponse(question);
    }

    public List<QuesResponse> getQuesResponsesByIds(List<Integer> ids) {
        return questionRepository.findAllById(ids)
                .stream()
                .map(this::createResponse)
                .toList();
    }

    private Question editQuestion(Question question, QuestionEditRequest request) {
        if (request.getParentId() != null)
            question.setParentId(request.getParentId());
        if (request.getSerial() != null)
            question.setSerial(request.getSerial());
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
        return question;
    }

    @Transactional
    public Question editQuestion(int id, QuestionEditRequest request) {
        Optional<Question> optionalQuestion = questionRepository.findById(id);
        if (optionalQuestion.isEmpty()) {
            throw new NotFoundException("question not found with id " + id);
        }

        Question question = optionalQuestion.get();

        if (QuesTypeEnum.MCQ.equals(question.getQuesType())) {
            optionService.editOptions(request.getOptionRequests(), question.getId());
        } else {
            request.getSubQuesRequests()
                    .forEach(subQuesRequest -> editQuestion(subQuesRequest.getId(), subQuesRequest));
        }

        return editQuestion(question, request);
    }

    @Transactional
    public void deleteQuestion(int id) {
        optionService.deleteByQuestionId(id);
        questionRepository.deleteById(id);
    }

    private Question incrementLikeCount(int id) {
        Optional<Question> optionalComment = findById(id);
        if (optionalComment.isEmpty()) {
            throw new NotFoundException("Comment not found with id + " + id);
        }

        optionalComment.get().setLikeCount(optionalComment.get().getLikeCount() + 1);
        return optionalComment.get();
    }

    @Transactional
    public Optional<Question> likeQuestion(int id) {
        Optional<Like> optionalLike = likeService.createLike(TypeEnum.QUESTION, id);
        if (optionalLike.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(incrementLikeCount(id));
    }
}
