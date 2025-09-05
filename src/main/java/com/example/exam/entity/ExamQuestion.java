package com.example.exam.entity;

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
@Table(name = "exam_questions", indexes = {
        @Index(name = "idx_exam_questions_exam_ques_id", columnList = "exam_id, question_id"),
})
@NoArgsConstructor
public class ExamQuestion extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @NotNull
    @Column(name = "question_id")
    private Integer questionId;

    @NotNull
    @Column(name = "exam_id")
    private Integer examId;

    @Column(name = "marks")
    private int marks = 1;

    @Column(name = "ans")
    private Integer ans;
}