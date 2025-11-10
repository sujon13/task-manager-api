package com.example.incident.specification;

import com.example.incident.enums.Division;
import com.example.incident.enums.IncidentCategory;
import com.example.incident.enums.IncidentStatus;
import com.example.incident.enums.Priority;
import com.example.incident.model.Incident;
import com.example.incident.model.IncidentFilterRequest;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

public class IncidentSpecification {
    private static Specification<Incident> hasData(final String data, final String entityField) {
        return (root, query, criteriaBuilder) ->
                StringUtils.hasText(data)
                        ? criteriaBuilder.equal(root.get(entityField), data)
                        : null;
    }

    private static Specification<Incident> hasStatus(IncidentStatus status) {
        return (root, query, cb) ->
                status != null
                        ? cb.equal(root.get("status"), status)
                        : null;
    }

    private static Specification<Incident> hasPriority(Priority priority) {
        return (root, query, cb) ->
                priority == null
                        ? null
                        : cb.equal(root.get("priority"), priority);
    }

    private static Specification<Incident> hasCategory(IncidentCategory category) {
        return (root, query, cb) ->
                category == null
                        ? null
                        : cb.equal(root.get("category"), category);
    }

    private static Specification<Incident> hasDivision(Division division) {
        return (root, query, cb) ->
                division == null
                        ? null
                        : cb.equal(root.get("division"), division);
    }

    private static Specification<Incident> hasReportedAtFrom(LocalDateTime from) {
        return (root, query, cb) ->
                from == null
                        ? null
                        : cb.greaterThanOrEqualTo(root.get("reportedAt"), from);
    }

    private static Specification<Incident> hasReportedAtTo(LocalDateTime to) {
        return (root, query, cb) ->
                to == null
                        ? null
                        : cb.lessThanOrEqualTo(root.get("reportedAt"), to);
    }

    private static Specification<Incident> hasResolvedAtFrom(LocalDateTime from) {
        return (root, query, cb) ->
                from == null
                        ? null
                        : cb.greaterThanOrEqualTo(root.get("resolvedAt"), from);
    }

    private static Specification<Incident> hasResolvedAtTo(LocalDateTime to) {
        return (root, query, cb) ->
                to == null
                        ? null
                        : cb.lessThanOrEqualTo(root.get("resolvedAt"), to);
    }

    public static Specification<Incident> hasPendingTo(List<String> pendingToList) {
        return (root, query, cb) -> {
            if (CollectionUtils.isEmpty(pendingToList)) {
                return cb.conjunction(); // no filter
            }
            return root.get("pendingTo").in(pendingToList);
        };
    }

    public static Specification<Incident> buildSpecification(IncidentFilterRequest request) {
        return Specification
                .where(hasData(request.getReportedBy(), "reportedBy"))
                .and(hasData(request.getAssignedTo(), "assignedTo"))
                .and(hasPendingTo(request.getPendingToList()))
                .and(hasPriority(request.getPriority()))
                .and(hasStatus(request.getStatus()))
                .and(hasCategory(request.getCategory()))
                .and(hasDivision(request.getDivision()))
                .and(hasReportedAtFrom(request.getReportedAtFrom()))
                .and(hasReportedAtTo(request.getReportedAtTo()))
                .and(hasResolvedAtFrom(request.getResolvedAtFrom()))
                .and(hasResolvedAtTo(request.getResolvedAtTo()));
    }
}
