package com.example.incident.model;

import com.example.incident.enums.IncidentStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StatusTrackerResponse {
    private String changedBy;

    @JsonFormat(pattern = "dd-MM-yyyy HH:mm")
    private LocalDateTime changedAt;

    private IncidentStatus status;
}
