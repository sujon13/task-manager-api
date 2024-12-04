package com.example.exam.model;

import com.example.exam.enums.ExamType;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExamEditRequest {
    private Integer id;

    @Size(max = 127, message = "Name must be 127 characters or less")
    private String name;

    @Size(max = 255, message = "Description must be 50 characters or less")
    private String description;

    @JsonFormat(pattern = "dd-MM-yyyy HH:mm")
    private LocalDateTime startTime;

    private Integer allocatedTimeInMin;

    private ExamType examType;
    private Integer totalQuestions;
    private Double totalMarks;
    private Integer postId;
    private Integer examTakerId;
}