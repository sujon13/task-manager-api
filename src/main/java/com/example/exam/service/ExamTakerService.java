package com.example.exam.service;

import com.example.exam.entity.ExamTaker;
import com.example.exam.model.ExamTakerRequest;
import com.example.exam.repository.ExamTakerRepository;
import com.example.exam.specification.ExamTakerSpecification;
import com.example.exception.NotFoundException;
import com.example.util.UserUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
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
    private final ExamTakerRepository examTakerRepository;

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

    public Page<ExamTaker> findAll(ExamTakerRequest request, Pageable pageable) {
        Specification<ExamTaker> examtakerSpecification = ExamTakerSpecification.buildSpecification(request);
        return ExamTakerRepository.findAll(examtakerSpecification, pageable);
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

    @Transactional
    public void deleteById(final int id) {
        examTakerRepository.deleteById(id);
    }
}
