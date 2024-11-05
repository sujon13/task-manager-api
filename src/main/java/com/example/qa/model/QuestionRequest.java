package com.example.qa.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuestionRequest {
    private int type = 1;
    private int version = 1;
    private String questionEn;
    private String questionBn;
    private Integer mcqAns;
    private boolean visible = true;

    private List<OptionRequest> optionsRequests = new ArrayList<>();
}