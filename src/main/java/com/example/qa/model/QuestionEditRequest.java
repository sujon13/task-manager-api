package com.example.qa.model;

import com.example.qa.enums.QuestionVersion;
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
    private Integer parentId;
    private Integer serial;
    private QuestionVersion version;

    private String questionEn;
    private String questionBn;
    private Integer mcqAns;

    private Boolean visible;

    @Builder.Default
    private List<OptionRequest> optionRequests = new ArrayList<>();

    @Builder.Default
    private List<QuestionEditRequest> subQuesRequests = new ArrayList<>();
}