package com.example.qa.model;

import com.example.qa.enums.QuesTypeEnum;
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
public class QuestionRequest {
    private Integer parentId;

    private Integer serial;

    @Builder.Default
    private QuesTypeEnum QuesType = QuesTypeEnum.MCQ;
    @Builder.Default
    private QuestionVersion version = QuestionVersion.ENGLISH;

    private Integer topicId;
    @Size(max = 1024)
    private String questionEn;
    @Size(max = 512)
    private String questionBn;
    private Integer mcqAns;
    @Size(max = 4096)
    private String explanation;
    private boolean visible = true;

    @Builder.Default
    private List<OptionRequest> optionRequests = new ArrayList<>();
    @Builder.Default
    private List<QuestionRequest> subQuesRequests = new ArrayList<>();
}