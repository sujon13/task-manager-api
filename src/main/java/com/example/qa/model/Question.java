package com.example.qa.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Setter
@Getter
@Entity
@Table(name = "questions")
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @NotNull
    @Column(name = "type", updatable = false)
    private Integer type = 1; // 1 for mcq

    @NotNull
    @Column(name = "questioner_id")
    private Integer questionerId;

    @NotNull
    @Column(name = "version", updatable = false)
    private Integer version = 1; // 1 for english

    @Column(name = "question_en", length = 512)
    private String questionEn;

    @Column(name = "question_bn", length = 512)
    private String questionBn;

    @Column(name = "mcq_ans")
    private Integer mcqAns; // id of option

    @Column(name = "visible")
    private boolean visible = true;

    @Column(name = "like_count")
    private Integer likeCount = 0;

    @Column(name = "freq_count")
    private Integer freqCount = 0;

}