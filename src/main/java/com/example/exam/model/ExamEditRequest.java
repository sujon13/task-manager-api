package com.example.exam.model;

import com.example.exam.enums.ExamType;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class ExamEditRequest {
    private Integer id;

    @NotBlank(message = "Name cannot be empty")
    @Size(max = 127, message = "Name must be 127 characters or less")
    private String name;

    @Size(max = 255, message = "Description must be 50 characters or less")
    private String description;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime startTime;

    private Integer allocatedTimeInMin;

    private ExamType examType;
    @Positive
    private Integer totalQuestions;
    @Positive
    @NotNull
    private Double totalMarks;
    private Integer postId;
    private Integer examTakerId;
}