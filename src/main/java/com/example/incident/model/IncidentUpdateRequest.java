package com.example.incident.model;

import com.example.incident.enums.IncidentStatus;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IncidentUpdateRequest {
    @NotNull
    private IncidentStatus status;
}
