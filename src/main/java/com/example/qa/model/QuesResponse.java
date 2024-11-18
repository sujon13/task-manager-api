package com.example.qa.model;

import com.example.qa.enums.QuesTypeEnum;
import com.example.qa.enums.QuestionVersion;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
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

    @Builder.Default
    private List<OptionResponse> optionResponses = new ArrayList<>();

    @Builder.Default
    private List<QuesResponse> subResponses = new ArrayList<>();
}
