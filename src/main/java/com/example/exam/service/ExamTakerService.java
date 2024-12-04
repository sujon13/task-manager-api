package com.example.exam.service;

import com.example.util.UserUtil;
import com.example.exam.entity.ExamTaker;
import com.example.exam.model.ExamTakerRequest;
import com.example.exam.repository.ExamTakerRepository;
import com.example.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ExamTakerService {
    private final ExamTakerRepository ExamTakerRepository;
    private final UserUtil userUtil;

    private ExamTaker buildExamTaker(ExamTakerRequest request) {
        ExamTaker examTaker = new ExamTaker();
        BeanUtils.copyProperties(request, examTaker);
        return examTaker;
    }

    @Transactional
    public ExamTaker addExamTaker(ExamTakerRequest request) {
        ExamTaker examTaker = buildExamTaker(request);
        return ExamTakerRepository.save(examTaker);
    }

    Optional<ExamTaker> findById(final Integer id) {
        if (id == null)
            return Optional.empty();

        return ExamTakerRepository.findById(id);
    }

    public ExamTaker getExamTaker(final int id) {
        return findById(id)
                .orElseThrow(() -> new NotFoundException("ExamTaker not found with id " + id));
    }

    public List<ExamTaker> findAllByIds(final Collection<Integer> ids) {
        return ExamTakerRepository.findAllById(ids);
    }

    public List<ExamTaker> findAll() {
        return ExamTakerRepository.findAll();
    }

    private void editExamTaker(ExamTaker examTaker, ExamTakerRequest examTakerRequest) {
        if (examTakerRequest.getEngName() != null) {
            examTaker.setEngName(examTakerRequest.getEngName());
        }
        if (examTakerRequest.getBngName() != null) {
            examTaker.setBngName(examTakerRequest.getBngName());
        }
        if (examTakerRequest.getDescription() != null) {
            examTaker.setDescription(examTakerRequest.getDescription());
        }
    }

    @Transactional
    public ExamTaker editExamTaker(final int id, ExamTakerRequest ExamTakerRequest) {
        ExamTaker examTaker = getExamTaker(id);
        if (!userUtil.hasEditPermission(examTaker)) {
            throw new AccessDeniedException("You do not have permission to edit this exam taker");
        }
        editExamTaker(examTaker, ExamTakerRequest);
        return examTaker;
    }
}
