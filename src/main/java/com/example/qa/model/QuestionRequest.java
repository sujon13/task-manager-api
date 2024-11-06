package com.example.qa.model;

import com.fasterxml.jackson.annotation.JsonProperty;
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

    @JsonProperty("question_en")
    private String questionEn;
    @JsonProperty("question_bn")
    private String questionBn;
    @JsonProperty("mcq_ans")
    private Integer mcqAns;
    private boolean visible = true;

    @JsonProperty("option_requests")
    private List<OptionRequest> optionRequests = new ArrayList<>();
}