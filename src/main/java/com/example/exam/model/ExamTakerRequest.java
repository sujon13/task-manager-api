package com.example.exam.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@Setter
@Getter
@Builder
public class ExamTakerRequest {
    private Integer id;
    @Length(max = 256)
    private String engName;
    @Length(max = 128)
    private String bngName;
    @Length(max = 512)
    private String description;
}