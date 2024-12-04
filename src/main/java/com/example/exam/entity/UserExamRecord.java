package com.example.exam.entity;

import com.example.exam.converter.UserExamStatusConverter;
import com.example.exam.enums.UserExamStatus;
import com.example.qa.model.Auditable;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Setter
@Getter
@Entity
@Table(name = "user_exam_records", indexes = {
        @Index(name = "idx_exam_records_examId_examinee", columnList = "exam_id, examinee")
})
@NoArgsConstructor
@AllArgsConstructor
public class UserExamRecord extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "exam_id", nullable = false)
    private Integer examId;

    @Column(name = "user_exam_status")
    @Convert(converter = UserExamStatusConverter.class)
    private UserExamStatus userExamStatus;

    @Column(name = "examinee", nullable = false)
    private String examinee;
}

