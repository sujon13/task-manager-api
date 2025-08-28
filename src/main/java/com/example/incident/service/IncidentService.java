package com.example.incident.service;

import com.example.exception.NotFoundException;
import com.example.incident.model.Incident;
import com.example.incident.model.IncidentResponse;
import com.example.incident.repository.IncidentRepository;
import com.example.qa.model.Question;
import com.example.qa.model.QuestionRequest;
import com.example.util.UserUtil;
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
public class IncidentService {
    private final IncidentRepository incidentRepository;
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
        if (request.getVisible() != null)
            question.setVisible(request.getVisible());
        return question;
    }

    /*
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
            incidentRepository.save(question);
            if (QuesTypeEnum.MCQ.equals(request.getQuesType())) {
                optionService.createOptions(request.getOptionRequests(), question.getId());
            } else {
                createSubQuestions(question.getId(), request.getSubQuesRequests());
            }
            return question;
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }*/

    public List<Incident> findAllByIds(List<Integer> ids) {
        return incidentRepository.findAllById(ids);
    }

    public Incident findById(int id) {
        return incidentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Incident not found with id " + id));
    }

    /*
    private QuesResponse createResponse(Question question, Topic topic, List<OptionResponse> optionResponses) {
        QuesResponse quesResponse = new QuesResponse();
        BeanUtils.copyProperties(question, quesResponse);

        quesResponse.setTopic(topic);

        if (QuesTypeEnum.MCQ.equals(question.getQuesType())) {
            quesResponse.setOptions(optionResponses);
        } else {
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

    private Question editQuestion(Question question, QuestionRequest request) {
        if (request.getParentId() != null)
            question.setParentId(request.getParentId());
        if (request.getSerial() != null)
            question.setSerial(request.getSerial());
        if (request.getVersion() != null)
            question.setVersion(request.getVersion());
        if (request.getTopicId() != null)
            question.setTopicId(request.getTopicId());

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
    public Question editQuestion(final int id, QuestionRequest request) {
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
    */


    @Transactional
    public void deleteIncident(int id) {
        incidentRepository.deleteById(id);
    }

    private IncidentResponse buildIncidentResponse(Incident incident) {
        IncidentResponse incidentResponse = new IncidentResponse();
        BeanUtils.copyProperties(incident, incidentResponse);
        return incidentResponse;
    }

    private List<IncidentResponse> buildIncidentResponses(List<Incident> incidents) {
        return incidents.stream()
                .map(this::buildIncidentResponse)
                .toList();
    }

    public List<IncidentResponse> getIncidents() {
        List<Incident> incidentList = incidentRepository.findAll();
        return buildIncidentResponses(incidentList);
    }

    public IncidentResponse getIncident(int id) {
        Incident incident = findById(id);
        return buildIncidentResponse(incident);
    }
}
