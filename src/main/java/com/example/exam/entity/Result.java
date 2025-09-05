package com.example.exam.entity;

import com.example.exam.converter.ExamTypeConverter;
import com.example.exam.enums.ExamType;
import com.example.qa.model.Auditable;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "results", indexes = {
        @Index(name = "idx_results_examId_examinee", columnList = "exam_id, examinee")
})
@NoArgsConstructor
public class Result extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "exam_id")
    private Integer examId;

    @NotNull
    @Column(name = "exam_type", updatable = false)
    @Convert(converter = ExamTypeConverter.class)
    private ExamType examType;

    @Column(name = "examinee")
    private String examinee;

    @Column(name = "marks_obtained")
    private Double marksObtained;

    @Column(name = "position")
    private Integer position;
}
