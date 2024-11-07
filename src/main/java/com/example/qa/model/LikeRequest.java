package com.example.qa.model;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LikeRequest {
    private Integer id;

    @NotNull
    private Integer type;

    @NotNull
    private Integer parentId;
}