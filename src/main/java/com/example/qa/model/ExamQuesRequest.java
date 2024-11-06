package com.example.qa.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.*;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExamQuesRequest {
    private Integer id;

    @NotNull
    @JsonProperty("question_id")
    private Integer questionId;

    @NotNull
    @JsonProperty("exam_id")
    private Integer examId;
}