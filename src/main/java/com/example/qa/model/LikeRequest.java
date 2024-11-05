package com.example.qa.model;

import com.fasterxml.jackson.annotation.JsonProperty;
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
    @JsonProperty("parent_id")
    private Integer parentId;
}