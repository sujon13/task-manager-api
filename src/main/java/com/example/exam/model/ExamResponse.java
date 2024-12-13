package com.example.exam.model;

import com.example.exam.entity.ExamTaker;
import com.example.exam.entity.Post;
import com.example.exam.enums.ExamStatus;
import com.example.exam.enums.ExamType;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExamResponse {
    private int id;
    private String name;
    private String description;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime startTime;
    private int allocatedTimeInMin;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime endTime;
    private ExamStatus status;
    private ExamType examType;
    private Integer totalQuestions;
    private Double totalMarks;
    private Double marksObtained;
    private Integer position;
    private Post post;
    private ExamTaker examTaker;
}
