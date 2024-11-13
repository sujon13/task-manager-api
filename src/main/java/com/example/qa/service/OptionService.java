package com.example.qa.service;

import com.example.qa.model.Option;
import com.example.qa.model.OptionRequest;
import com.example.qa.model.OptionResponse;
import com.example.qa.repository.OptionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OptionService {
    private final OptionRepository optionRepository;

    private Option buildOption(OptionRequest request) {
        Option option = new Option();
        option.setQuestionId(request.getQuestionId());
        option.setSerial(request.getSerial());
        option.setValueEn(request.getValueEn());
        option.setValueBn(request.getValueBn());
        return option;
    }

    @Transactional
    public OptionRequest createOption(OptionRequest request) {
        Option option = buildOption(request);
        try {
            optionRepository.save(option);
            return createOptionRequest(option);
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }

    @Transactional
    public List<OptionRequest> createOptions(List<OptionRequest> requests, int questionId) {
        return requests.stream()
                .peek(request -> request.setQuestionId(questionId))
                .map(this::createOption)
                .toList();
    }

    private OptionRequest createOptionRequest(Option option) {
        return OptionRequest.builder()
                .id(option.getId())
                .serial(option.getSerial())
                .valueEn(option.getValueEn())
                .valueBn(option.getValueBn())
                .build();
    }

    public Optional<Option> findById(int id) {
        return optionRepository.findById(id);
    }

    private OptionResponse createOptionResponse(Option option) {
        OptionResponse optionResponse = new OptionResponse();
        BeanUtils.copyProperties(option, optionResponse);
        return optionResponse;
    }

    public List<Option> findByQuestionId(int questionId) {
        return optionRepository.findAllByQuestionId(questionId);
    }

    public List<Option> findByQuestionIds(List<Integer> questionIds) {
        return optionRepository.findAllByQuestionIdIn(questionIds);
    }

    public List<OptionResponse> getOptionResponsesByQuestionId(int questionId) {
        List<Option> options = findByQuestionId(questionId);
        return options.stream()
                .map(this::createOptionResponse)
                .toList();
    }

    public Map<Integer, List<OptionResponse>> getQuesIdToOptionResponesMap(List<Integer> questionIds) {
        return findByQuestionIds(questionIds)
                .stream()
                .map(this::createOptionResponse)
                .collect(Collectors.groupingBy(OptionResponse::getQuestionId));
    }

    private Option editOption(OptionRequest request, Option option) {
        if (request.getSerial() != null)
            option.setSerial(request.getSerial());
        if (request.getValueEn() != null)
            option.setValueEn(request.getValueEn());
        if (request.getValueBn() != null)
            option.setValueBn(request.getValueBn());

        return option;
    }

    @Transactional
    public void editOptions(List<OptionRequest> optionRequests, int questionId) {
        Map<Integer, Option> idToOptionMap = optionRepository.findAllByQuestionId(questionId)
                .stream()
                .collect(Collectors.toMap(Option::getId, Function.identity()));
        optionRequests.forEach(request -> editOption(request, idToOptionMap.get(request.getId())));
    }

    @Transactional
    public void deleteOption(int id) {
        optionRepository.deleteById(id);
    }

    @Transactional
    public void deleteByQuestionId(int questionId) {
        optionRepository.deleteAllByQuestionId(questionId);
    }
}
