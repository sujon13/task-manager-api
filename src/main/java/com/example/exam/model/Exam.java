package com.example.exam.model;

import com.example.exam.converter.ExamTypeConverter;
import com.example.exam.enums.ExamType;
import com.example.qa.model.Auditable;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;


@Setter
@Getter
@Entity
@Table(name = "exams")
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Exam extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

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

    @NotNull
    @Column(name = "exam_type", updatable = false)
    @Convert(converter = ExamTypeConverter.class)
    private ExamType examType = ExamType.PRACTICE;

    @Column(name = "total_questions")
    private Integer totalQuestions;

    @Column(name = "total_marks")
    private Double totalMarks;
}