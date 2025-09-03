package com.example.incident.model;

import jakarta.validation.constraints.Size;
import lombok.*;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateRequestByReporter {
    private Integer id;

    @Size(max = 2048)
    private String remarksByReporter;
}