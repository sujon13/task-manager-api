package com.example.qa.service;

import com.example.qa.model.Option;
import com.example.qa.model.OptionRequest;
import com.example.qa.repository.OptionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    public Option createOption(OptionRequest request) {
        Option option = buildOption(request);
        try {
            optionRepository.save(option);
            return option;
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }

    @Transactional
    public List<Option> createOptions(List<OptionRequest> requests, int questionId) {
        return requests.stream()
                .peek(request -> request.setQuestionId(questionId))
                .map(this::createOption)
                .toList();
    }

    public Optional<Option> findById(int id) {
        return optionRepository.findById(id);
    }

    public List<Option> findByQuestionId(int questionId) {
        return optionRepository.findAllByQuestionId(questionId);
    }

    @Transactional
    public Option editOption(OptionRequest request) {
        Optional<Option> optionalOption = optionRepository.findById(request.getId());
        if (optionalOption.isEmpty()) {
            throw new RuntimeException("Option not found");
        }

        Option option = optionalOption.get();
        editOption(request, option);

        return option;
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
