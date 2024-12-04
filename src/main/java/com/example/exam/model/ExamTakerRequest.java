package com.example.exam.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
public class ExamTakerRequest {
    private Integer id;
    private String engName;
    private String bngName;
    private String description;
}