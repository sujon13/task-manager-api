package com.example.incident.model;

import com.example.incident.enums.Division;
import com.example.incident.enums.IncidentCategory;
import com.example.incident.enums.IncidentStatus;
import com.example.incident.enums.Priority;
import com.example.util.DateUtil;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class IncidentFilterRequest {
    @Size(max = 64)
    private String reportedBy;

    @Size(max = 64)
    private String assignedTo;

    private List<String> pendingToList;

    private IncidentStatus status;

    private Priority priority;

    private IncidentCategory category;

    private Division division;

    @JsonFormat(pattern = "dd-MM-yyyy HH:mm")
    private LocalDateTime reportedAtFrom;

    @JsonFormat(pattern = "dd-MM-yyyy HH:mm")
    private LocalDateTime reportedAtTo;

    @JsonFormat(pattern = "dd-MM-yyyy HH:mm")
    private LocalDateTime resolvedAtFrom;

    @JsonFormat(pattern = "dd-MM-yyyy HH:mm")
    private LocalDateTime resolvedAtTo;

    public void setReportedBy(String value) {
        if (value == null || value.equalsIgnoreCase("null") || value.isBlank()) {
            this.reportedBy = null;
        } else {
            this.reportedBy = value;
        }
    }

    public void setAssignedTo(String value) {
        if (value == null || value.equalsIgnoreCase("null") || value.isBlank()) {
            this.assignedTo = null;
        }  else {
            this.assignedTo = value;
        }
    }

    public void setPendingToList(List<String> value) {
        if (value == null || value.isEmpty()) {
            this.pendingToList = List.of();
        } else {
            this.pendingToList = value;
        }
    }

    public void setPriority(String value) {
        if (value == null || value.equalsIgnoreCase("null") || value.isBlank()) {
            this.priority = null;
        } else {
            this.priority = Priority.valueOf(value.toUpperCase());
        }
    }

    public void setStatus(String value) {
        if (value == null || value.equalsIgnoreCase("null") || value.isBlank()) {
            this.status= null;
        } else {
            this.status = IncidentStatus.valueOf(value.toUpperCase());
        }
    }

    public void setReportedAtFrom(String value) {
        if (value == null || value.equalsIgnoreCase("null") || value.isBlank()) {
            this.reportedAtFrom = null;
        } else {
            this.reportedAtFrom = DateUtil.parseDate(value);
        }
    }

    public void setReportedAtTo(String value) {
        if (value == null || value.equalsIgnoreCase("null") || value.isBlank()) {
            this.reportedAtTo = null;
        } else {
            this.reportedAtTo = DateUtil.parseDate(value);
        }
    }

    public void setResolvedAtFrom(String value) {
        if (value == null || value.equalsIgnoreCase("null") || value.isBlank()) {
            this.resolvedAtFrom = null;
        } else {
            this.resolvedAtFrom = DateUtil.parseDate(value);
        }
    }

    public void setResolvedAtTo(String value) {
        if (value == null || value.equalsIgnoreCase("null") || value.isBlank()) {
            this.resolvedAtTo = null;
        } else {
            this.resolvedAtTo = DateUtil.parseDate(value);
        }
    }

    public void setCategory(String value) {
        if (value == null || value.equalsIgnoreCase("null") || value.isBlank()) {
            this.category = null;
        } else {
            this.category = IncidentCategory.valueOf(value.toUpperCase());
        }
    }

    public void setDivision(String value) {
        if (value == null || value.equalsIgnoreCase("null") || value.isBlank()) {
            this.division = null;
        } else {
            this.division = Division.valueOf(value.toUpperCase());
        }
    }
}
