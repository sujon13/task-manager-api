package com.example.incident.model;

import com.example.auth.model.UserResponse;
import com.example.incident.enums.FaultNature;
import com.example.incident.enums.IncidentStatus;
import com.example.incident.enums.Priority;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class IncidentResponse {
    private int id;
    private int eventNo;
    private String station;
    private String location;

    @JsonFormat(pattern = "dd-MM-yyyy HH:mm")
    private LocalDateTime occurredAt;

    @JsonFormat(pattern = "dd-MM-yyyy HH:mm")
    private LocalDateTime reportedAt;

    private UserResponse reportedBy;
    private UserResponse assignedTo;

    @JsonFormat(pattern = "dd-MM-yyyy HH:mm")
    private LocalDateTime resolvedAt;

    private FaultNature faultNature;

    // Affected Equipment
    List<String> affectedEquipments;

    private String summary;
    private String description;

    // actionTakenByScada
    private List<ActionTakenRequest> actionsTakenByReporter;

    // actionTakenByContractor
    private List<ActionTakenRequest> actionsTakenByAssignee;

    private String remarksByReporter;
    private String remarksByAssignee;
    private IncidentStatus status;
    private String statusStr;
    private Priority priority;

    // is Reporter or Assignee
    private boolean isReporter = false;
    private boolean isAssignee = false;

    // category
    // sub-category

}
