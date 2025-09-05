package com.example.qa.model;

import com.example.qa.enums.QuesTypeEnum;
import com.example.qa.enums.QuestionVersion;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
@NoArgsConstructor
public class QuestionRequest {
    private Integer id;
    private Integer parentId;

    private Integer serial;

    private QuesTypeEnum QuesType = QuesTypeEnum.MCQ;
    private QuestionVersion version = QuestionVersion.ENGLISH;

    private Integer topicId;
    @Size(max = 1024)
    private String questionEn;
    @Size(max = 512)
    private String questionBn;
    private Integer mcqAns;
    @Size(max = 4096)
    private String explanation;
    private Boolean visible;

    private List<OptionRequest> optionRequests = new ArrayList<>();
    private List<QuestionRequest> subQuesRequests = new ArrayList<>();
}