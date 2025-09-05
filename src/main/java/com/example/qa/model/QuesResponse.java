package com.example.qa.model;

import com.example.qa.enums.QuesTypeEnum;
import com.example.qa.enums.QuestionVersion;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class QuesResponse extends Auditable {
    private Integer id;
    private Integer parentId;
    private Integer serial;
    private QuesTypeEnum quesType;
    private String questionerUserName;
    private QuestionVersion version;
    private Topic topic;
    private String questionEn;
    private String questionBn;
    private Integer mcqAns;
    private Integer givenAns;
    private String explanation;
    private boolean visible;
    private Integer likeCount;
    private Integer freqCount;

    private List<OptionResponse> options = new ArrayList<>();

    private List<QuesResponse> subResponses = new ArrayList<>();
}

