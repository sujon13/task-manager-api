package com.example.qa.model;

import com.example.qa.enums.QuestionVersion;
import jakarta.validation.constraints.Size;
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

    @Size(max = 1024)
    private String questionEn;
    @Size(max = 512)
    private String questionBn;
    private Integer mcqAns;
    @Size(max = 512)
    @Size(max = 512)
    private String explanation;
    private Boolean visible;

    @Builder.Default
    private List<OptionRequest> optionRequests = new ArrayList<>();

    @Builder.Default
    private List<QuestionEditRequest> subQuesRequests = new ArrayList<>();
}