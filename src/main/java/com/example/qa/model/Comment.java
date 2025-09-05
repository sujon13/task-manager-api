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
@Table(name = "comments", indexes = {
        @Index(name = "idx_comments_question_id", columnList = "question_id")
})
@NoArgsConstructor
public class Comment extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @NotNull
    @Column(name = "question_id", updatable = false)
    private Integer questionId;

    @Column(name = "description", length = 4096)
    private String description;

    @Column(name = "like_count")
    private int likeCount = 0;
}
