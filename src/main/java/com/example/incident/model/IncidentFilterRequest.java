package com.example.incident.model;

import com.example.incident.enums.IncidentStatus;
import com.example.incident.enums.Priority;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IncidentFilterRequest {
    @Size(max = 64)
    private String reportedBy;

    @Size(max = 64)
    private String assignedTo;

    private IncidentStatus status;

    private Priority priority;

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
}
