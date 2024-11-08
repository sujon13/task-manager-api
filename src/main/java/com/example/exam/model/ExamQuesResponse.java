package com.example.exam.model;

import com.example.exam.enums.ExamType;
import com.example.qa.model.QuesResponse;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExamQuesResponse {
    private int id;
    private String name;
    private String description;
    @JsonFormat(pattern = "dd-MM-yyyy HH:mm")
    private LocalDateTime startTime;
    private int allocatedTimeInMin;
    @JsonFormat(pattern = "dd-MM-yyyy HH:mm")
    private LocalDateTime endTime;
    private ExamType examType;
    private Integer totalQuestions;
    private Double totalMarks;
    private List<QuesResponse> questions = new ArrayList<>();
}