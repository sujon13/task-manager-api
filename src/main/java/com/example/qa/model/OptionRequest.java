package com.example.qa.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OptionRequest {
    private Integer id;
    @NotNull
    @JsonProperty("question_id")
    private Integer questionId;
    @NotBlank
    private Integer serial;
    @JsonProperty("value_en")
    private String valueEn;
    @JsonProperty("value_bn")
    private String valueBn;
}
