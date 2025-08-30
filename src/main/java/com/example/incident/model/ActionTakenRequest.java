package com.example.incident.model;

import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActionTakenRequest {
    private Integer id;

    @Size(min = 1, max = 1024)
    private String action;
}
