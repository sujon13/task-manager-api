package com.example.exam.model;

import com.example.exam.converter.ExamStatusConverter;
import com.example.exam.converter.ExamTypeConverter;
import com.example.exam.enums.ExamStatus;
import com.example.exam.enums.ExamType;
import com.example.qa.model.Auditable;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;


@Setter
@Getter
@Entity
@Table(name = "exams")
@NoArgsConstructor
@AllArgsConstructor
public class Exam extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "parent_id")
    private Integer parentId;

    @Column(name = "name")
    private String name;

    @Column(name = "description", length = 512)
    private String description;

    @Column(name = "start_time")
    private LocalDateTime startTime;

    @Column(name = "allocated_time_in_min")
    private int allocatedTimeInMin;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    @Column(name = "exam_status")
    @Convert(converter = ExamStatusConverter.class)
    private ExamStatus status = ExamStatus.NOT_SCHEDULED;

    @NotNull
    @Column(name = "exam_type", updatable = false)
    @Convert(converter = ExamTypeConverter.class)
    private ExamType examType = ExamType.PRACTICE;

    @Column(name = "total_questions")
    private Integer totalQuestions;

    @Column(name = "total_marks")
    private Double totalMarks;

    @Column(name = "examinee")
    private String examinee;
}