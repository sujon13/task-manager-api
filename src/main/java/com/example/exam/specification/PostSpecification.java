package com.example.exam.specification;


import com.example.exam.entity.Post;
import com.example.exam.model.PostRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

public class PostSpecification {

    private static Specification<Post> hasEngName(String engName) {
        return (root, query, criteriaBuilder) ->
                StringUtils.hasText(engName)
                        ? criteriaBuilder.like(criteriaBuilder.lower(root.get("engName")), "%" + engName.toLowerCase() + "%")
                        : null;
    }

    private static Specification<Post> hasBngName(String bngName) {
        return (root, query, criteriaBuilder) ->
                StringUtils.hasText(bngName)
                        ? criteriaBuilder.like(criteriaBuilder.lower(root.get("bngName")), "%" + bngName.toLowerCase() + "%")
                        : null;
    }

    private static Specification<Post> hasGrade(Integer grade) {
        return (root, query, criteriaBuilder) ->
                grade == null
                        ? null
                        : criteriaBuilder.equal(root.get("grade"), grade);
    }

    public static Specification<Post> buildSpecification(PostRequest request) {
        return Specification
                .where(hasEngName(request.getEngName()))
                .and(hasBngName(request.getBngName()))
                .and(hasGrade(request.getGrade()));
    }
}
