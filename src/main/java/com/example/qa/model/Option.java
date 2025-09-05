package com.example.qa.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "options")
@NoArgsConstructor
public class Option extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @NotNull
    @Column(name = "question_id", updatable = false)
    private Integer questionId;

    @Column(name = "serial")
    private Integer serial;

    @Column(name = "value_en")
    private String valueEn;

    @Column(name = "value_bn")
    private String valueBn;
}
