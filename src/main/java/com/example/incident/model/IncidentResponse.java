package com.example.incident.model;

import com.example.incident.enums.FaultNature;
import com.example.incident.enums.IncidentStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
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

    private String reportedBy;
    private String assignedTo;

    @JsonFormat(pattern = "dd-MM-yyyy HH:mm")
    private LocalDateTime resolvedAt;

    private FaultNature faultNature;

    // Affected Equipment
    List<String> affectedEquipments;

    private String summary;
    private String description;

    // actionTakenByScada
    private List<ActionTakenRequest> actionsTakenByScada;

    // actionTakenByContractor
    private List<ActionTakenRequest> actionsTakenByContractor;

    private String remarksByScada;
    private String remarksByContractor;
    private IncidentStatus status;

    // category
    // sub-category

}
