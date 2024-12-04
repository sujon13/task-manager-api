package com.example.exam.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class PostRequest {
    private Integer id;
    private String engName;
    private String bngName;
    private String description;
    private Integer grade;
}
