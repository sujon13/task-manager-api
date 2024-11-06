package com.example.qa.service;

import com.example.qa.UserUtil;
import com.example.qa.enums.TypeEnum;
import com.example.qa.exception.NotFoundException;
import com.example.qa.model.*;
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
    private final LikeService likeService;
    private final UserUtil userUtil;


    private Question buildQuestion(QuestionRequest request) {
        Question question = new Question();
        question.setType(request.getType());
        question.setQuestionerUserName(userUtil.getUserName()); //  need to update
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
            List<OptionRequest> optionRequestList =
                    optionService.createOptions(request.getOptionRequests(), question.getId());
            return question;
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }

    private QuestionEditRequest buildQuestionEditRequest(Question question, List<OptionRequest> optionRequestList) {
        return QuestionEditRequest.builder()
                .id(question.getId())
                .questionEn(question.getQuestionEn())
                .questionBn(question.getQuestionBn())
                .type(question.getType())
                .version(question.getVersion())
                .visible(question.isVisible())
                .mcqAns(question.getMcqAns())
                .optionRequests(optionRequestList)
                .build();
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

    private Question editQuestionAndOption(Question question, QuestionEditRequest request) {
        optionService.editOptions(request.getOptionRequests(), question.getId());
        editQuestion(question, request);
        return question;
    }

    @Transactional
    public Question editQuestion(int id, QuestionEditRequest request) {
        Optional<Question> optionalQuestion = questionRepository.findById(id);
        if (optionalQuestion.isEmpty()) {
            throw new NotFoundException("question not found with id " + id);
        }

        return editQuestionAndOption(optionalQuestion.get(), request);
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
        Optional<Like> optionalLike = likeService.createLike(TypeEnum.Question, id);
        if (optionalLike.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(incrementLikeCount(id));
    }
}
