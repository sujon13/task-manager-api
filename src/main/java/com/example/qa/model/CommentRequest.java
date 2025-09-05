package com.example.qa.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
public class CommentRequest {
    private Integer id;

    @NotNull
    private Integer questionId;

    @NotBlank
    private String description;
}