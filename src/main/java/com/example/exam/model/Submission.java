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

@Setter
@Getter
@Entity
@Table(name = "submissions")
@NoArgsConstructor
@AllArgsConstructor
public class Submission extends Auditable {
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

    @Column(name = "ques_id")
    private Integer quesId;

    @Column(name = "examinee_user_name")
    private String examineeUserName;

    @Column(name = "given_ans")
    private Integer givenAns;

    @Column(name = "is_correct")
    private boolean isCorrect = false;
}
