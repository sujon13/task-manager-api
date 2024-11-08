package com.example.exam.model;

import lombok.*;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExamQuesEditRequest {
    private Integer id;
    private Integer marks;
    private Integer ans;
}