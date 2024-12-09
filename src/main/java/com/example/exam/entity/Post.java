package com.example.exam.entity;

import com.example.qa.model.Auditable;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Setter
@Getter
@Entity
@Table(name = "posts")
@NoArgsConstructor
@AllArgsConstructor
public class Post extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "eng_name")
    private String engName;

    @Column(name = "bng_name")
    private String bngName;

    @Column(name = "description", length = 1024)
    private String description;

    @Column
    private Integer grade;
}