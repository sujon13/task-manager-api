package com.example.exam.specification;

import com.example.exam.entity.ExamTaker;
import com.example.exam.model.ExamTakerRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;


public class ExamTakerSpecification  {

    private static Specification<ExamTaker> hasEngName(String engName) {
        return (root, query, criteriaBuilder) ->
                StringUtils.hasText(engName)
                        ? criteriaBuilder.like(criteriaBuilder.lower(root.get("engName")), "%" + engName.toLowerCase() + "%")
                        : null;
    }

    private static Specification<ExamTaker> hasBngName(String bngName) {
        return (root, query, criteriaBuilder) ->
                StringUtils.hasText(bngName)
                        ? criteriaBuilder.like(criteriaBuilder.lower(root.get("bngName")), "%" + bngName.toLowerCase() + "%")
                        : null;
    }

    public static Specification<ExamTaker> buildSpecification(ExamTakerRequest request) {
        return Specification
                .where(hasEngName(request.getEngName()))
                .and(hasBngName(request.getBngName()));
    }
}
