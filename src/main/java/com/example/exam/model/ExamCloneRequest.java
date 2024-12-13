package com.example.exam.model;

import com.example.exam.enums.ExamType;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.time.LocalDateTime;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExamCloneRequest {
    @NotNull
    private Integer parentExamId;

    @Builder.Default
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime startTime = LocalDateTime.now();

    @NotNull
    private Integer allocatedTimeInMin;

    @NotNull
    private ExamType examType;

    @Positive
    private Integer totalQuestions;
    @Positive
    private Double totalMarks;

    private Integer postId;
    private Integer examTakerId;
}