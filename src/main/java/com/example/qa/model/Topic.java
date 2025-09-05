package com.example.qa.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "topics")
@NoArgsConstructor
public class Topic extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "parent_id")
    private Integer parentId;

    @Column(name = "eng_name")
    private String engName;

    @Column(name = "bng_name")
    private String bngName;
}
