package com.example.qa.model;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuestionEditRequest {
    private Integer id;
    private Integer type;
    private Integer version;
    private String questionEn;
    private String questionBn;
    private Integer mcqAns;
    private Boolean visible;

    private List<OptionRequest> optionsRequests = new ArrayList<>();
}