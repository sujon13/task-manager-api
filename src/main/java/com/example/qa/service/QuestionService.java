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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class QuestionService {
    private final QuestionRepository questionRepository;
    private final OptionService optionService;
    private final LikeService likeService;
    private final TopicService topicService;
    private final UserUtil userUtil;


    private Question buildQuestion(QuestionRequest request) {
        Question question = new Question();
        question.setParentId(request.getParentId());
        question.setSerial(request.getSerial());
        question.setQuesType(request.getQuesType());
        question.setQuestionerUserName(userUtil.getUserName()); //  need to update
        question.setVersion(request.getVersion());
        question.setTopicId(request.getTopicId());
        question.setQuestionEn(request.getQuestionEn());
        question.setQuestionBn(request.getQuestionBn());
        question.setMcqAns(request.getMcqAns()); // 1 (a) 2 (b) (1-5)
        question.setExplanation(request.getExplanation());
        question.setVisible(request.isVisible());
        return question;
    }

    private void createSubQuestions(Integer parentId, List<QuestionRequest> subQuestionRequests) {
        subQuestionRequests
                .forEach(subQuesRequest -> {
                    subQuesRequest.setParentId(parentId);
                    createQuestion(subQuesRequest);
                });
    }

    @Transactional
    public Question createQuestion(QuestionRequest request) {
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

    public List<Question> findAllByIds(List<Integer> ids) {
        return questionRepository.findAllById(ids);
    }

    public Question getQuestion(int id) {
        return findById(id)
                .orElseThrow(() -> new NotFoundException("Question not found with id " + id));
    }

    private QuesResponse createResponse(Question question, Topic topic, List<OptionResponse> optionResponses) {
        QuesResponse quesResponse = new QuesResponse();
        BeanUtils.copyProperties(question, quesResponse);

        quesResponse.setTopic(topic);

        if (QuesTypeEnum.MCQ.equals(question.getQuesType())) {
            quesResponse.setOptionResponses(optionResponses);
        } else {
            List<QuesResponse> subQuesResponses = questionRepository.findAllByParentId(question.getId())
                    .stream()
                    .map(this::createResponse)
                    .toList();
            quesResponse.setSubResponses(subQuesResponses);
        }
        return quesResponse;
    }

    private QuesResponse createResponse(Question question) {
        Topic topic = topicService.findById(question.getTopicId()).orElse(null);
        List<OptionResponse> optionResponses = optionService.getOptionResponsesByQuestionId(question.getId());
        return createResponse(question, topic, optionResponses);
    }

    public QuesResponse getQuesResponseById(int id) {
        Question question = getQuestion(id);
        return createResponse(question);
    }

    private Map<Integer, Topic> getTopicIdToTopicMap(List<Question> questions) {
        Set<Integer> topicIds = questions.stream()
                .map(Question::getTopicId)
                .collect(Collectors.toUnmodifiableSet());

        return topicService.findAllByIds(topicIds)
                .stream()
                .collect(Collectors.toMap(Topic::getId, Function.identity()));
    }

    private Map<Integer, List<OptionResponse>> getQuesIdToOptionResponseMap(List<Question> questions) {
       List<Integer> questionIds = questions.stream()
               .map(Question::getId)
               .toList();
       return optionService.getQuesIdToOptionResponesMap(questionIds);
    }

    private List<QuesResponse> getQuesResponses(List<Question> questions, Map<Integer, Topic> topicIdToTopicMap) {
        Map<Integer, List<OptionResponse>> quesIdToOptionResponsesMap = getQuesIdToOptionResponseMap(questions);

        return questions
                .stream()
                .map(question -> createResponse(question,
                        topicIdToTopicMap.get(question.getTopicId()),
                        quesIdToOptionResponsesMap.get(question.getId()))
                )
                .toList();
    }

    private List<QuesResponse> getQuesResponses(List<Question> questions) {
        Map<Integer, Topic> topicIdToTopicMap = getTopicIdToTopicMap(questions);
        return getQuesResponses(questions, topicIdToTopicMap);
    }

    public List<QuesResponse> getQuesResponsesByIds(List<Integer> ids) {
        List<Question> questions = findAllByIds(ids);
        return getQuesResponses(questions);
    }

    public Page<QuesResponse> getQuesResponsesByTopicId(final int topicId, Pageable pageable) {
        List<Topic> subTopics = topicService.findAllSubTopics(topicId);
        List<Integer> topicIds = subTopics.stream().map(Topic::getId).toList();
        Page<Question> questions = questionRepository.findAllByTopicIdIn(topicIds, pageable);

        Map<Integer, Topic> topicIdToTopicMap = subTopics.stream()
                .collect(Collectors.toMap(Topic::getId, Function.identity()));
        List<QuesResponse> quesResponses = getQuesResponses(questions.getContent(), topicIdToTopicMap);
        return new PageImpl<>(quesResponses, pageable, questions.getTotalElements());
    }

    private Question editQuestion(Question question, QuestionEditRequest request) {
        if (request.getParentId() != null)
            question.setParentId(request.getParentId());
        if (request.getSerial() != null)
            question.setSerial(request.getSerial());
        if (request.getVersion() != null)
            question.setVersion(request.getVersion());
        // should we update topic?

        if (request.getQuestionEn() != null)
            question.setQuestionEn(request.getQuestionEn());
        if (request.getQuestionBn() != null)
            question.setQuestionBn(request.getQuestionBn());
        if (request.getMcqAns() != null)
            question.setMcqAns(request.getMcqAns());
        if (request.getExplanation() != null)
            question.setExplanation(request.getExplanation());
        if (request.getVisible() != null)
            question.setVisible(request.getVisible());
        return question;
    }

    @Transactional
    public Question editQuestion(int id, QuestionEditRequest request) {
        Question question = getQuestion(id);

        if (!userUtil.hasEditPermission(question)) {
            throw new AccessDeniedException("You do not have permission to edit this question");
        }


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
        Question question = getQuestion(id);

        question.setLikeCount(question.getLikeCount() + 1);
        return question;
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
