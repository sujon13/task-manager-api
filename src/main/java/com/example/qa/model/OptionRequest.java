package com.example.qa.model;

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
    private Integer questionId;
    @NotBlank
    private String serial;
    private String valueEn;
    private String valueBn;
}
