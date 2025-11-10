package com.example.incident.model;

import com.example.incident.enums.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;


@Getter
@Setter
@NoArgsConstructor
public class IncidentRequest {
    @Size(max = 64)
    private String station;

    @Size(max = 64)
    private String location;

    @JsonFormat(pattern = "dd-MM-yyyy HH:mm")
    private LocalDateTime occurredAt;

    @JsonFormat(pattern = "dd-MM-yyyy HH:mm")
    private LocalDateTime reportedAt = LocalDateTime.now();

    @Size(max = 64)
    private String assignedTo;

    @JsonFormat(pattern = "dd-MM-yyyy HH:mm")
    private LocalDateTime resolvedAt;

    private FaultNature faultNature = FaultNature.SOFTWARE;

    // Affected Equipment
    private List<String> affectedEquipments = List.of();

    @NotEmpty
    @Size(max = 1024)
    private String summary;

    @Size(max = 2048)
    private String description;

    // actionTakenByScada
    private List<ActionTakenRequest> actionsTakenByReporter = List.of();

    // actionTakenByContractor
    private List<ActionTakenRequest> actionsTakenByAssignee = List.of();

//    @Size(max = 2048)
//    private String remarksByReporter;
//
//    @Size(max = 2048)
//    private String remarksByAssignee;

    private IncidentStatus status = IncidentStatus.REPORTED;

    private Priority priority = Priority.HIGH;

    private IncidentCategory category;

    private Division division = Division.SCADA;

    // category
    // sub-category

}
