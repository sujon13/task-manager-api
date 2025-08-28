package com.example.incident.model;

import com.example.incident.enums.FaultNature;
import com.example.incident.enums.IncidentStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IncidentRequest {
    @NotNull
    @Size(max = 64)
    private String station;

    @Size(max = 64)
    private String location;

    @JsonFormat(pattern = "dd-MM-yyyy HH:mm")
    private LocalDateTime occurredAt;

    @JsonFormat(pattern = "dd-MM-yyyy HH:mm")
    private LocalDateTime reportedAt = LocalDateTime.now();

    private String assignedTo;

    @JsonFormat(pattern = "dd-MM-yyyy HH:mm")
    private LocalDateTime resolvedAt;

    private FaultNature faultNature = FaultNature.SOFTWARE;

    // Affected Equipment

    @Size(max = 1024)
    private String summary;

    @Size(max = 4096)
    private String description;

    // actionTakenByScada

    // actionTakenByContractor

    @Size(max = 2048)
    private String remarksByScada;

    @Size(max = 2048)
    private String remarksByContractor;

    private IncidentStatus status = IncidentStatus.REPORTED;

    // category
    // sub-category

}
