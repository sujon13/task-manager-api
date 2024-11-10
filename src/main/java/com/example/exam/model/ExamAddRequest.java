package com.example.exam.model;

import com.example.exam.enums.ExamType;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExamAddRequest {
    private Integer id;

    @NotBlank(message = "Name cannot be empty")
    @Size(max = 127, message = "Name must be 127 characters or less")
    private String name;

    @Size(max = 255, message = "Description must be 255 characters or less")
    private String description;

    @JsonFormat(pattern = "dd-MM-yyyy HH:mm")
    private LocalDateTime startTime;

    @Builder.Default
    private int allocatedTimeInMin = 60;

    @NotNull
    private ExamType examType;
    private Integer totalQuestions;
    private Double totalMarks;
}