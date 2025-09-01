package com.example.incident.specification;

import com.example.incident.enums.IncidentStatus;
import com.example.incident.enums.Priority;
import com.example.incident.model.Incident;
import com.example.incident.model.IncidentFilterRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

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

    public static Specification<Incident> buildSpecification(IncidentFilterRequest request) {
        return Specification
                .where(hasData(request.getReportedBy(), "reportedBy"))
                .and(hasData(request.getAssignedTo(), "assignedTo"))
                .and(hasPriority(request.getPriority()))
                .and(hasStatus(request.getStatus()));
    }
}
