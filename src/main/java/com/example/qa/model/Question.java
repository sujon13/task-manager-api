package com.example.qa.model;

import com.example.qa.converter.QuesTypeConverter;
import com.example.qa.converter.QuesVersionConverter;
import com.example.qa.enums.QuesTypeEnum;
import com.example.qa.enums.QuestionVersion;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "questions")
@NoArgsConstructor
@AllArgsConstructor
public class Question extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "parent_id")
    private Integer parentId;

    @Column(name = "serial")
    private Integer serial;

    @NotNull
    @Column(name = "ques_type", updatable = false)
    @Convert(converter = QuesTypeConverter.class)
    private QuesTypeEnum quesType = QuesTypeEnum.MCQ;

    @NotNull
    @Column(name = "questioner_user_name")
    private String questionerUserName;

    @NotNull
    @Column(name = "version", updatable = false)
    @Convert(converter = QuesVersionConverter.class)
    private QuestionVersion version = QuestionVersion.ENGLISH;

    @Column(name = "question_en", length = 1024)
    private String questionEn;

    @Column(name = "question_bn", length = 1024)
    private String questionBn;

    @Column(name = "mcq_ans")
    private Integer mcqAns; // id of option

    @Column(name = "explanation", length = 8192)
    private String explanation;

    @Column(name = "visible")
    private boolean visible = true;

    @Column(name = "like_count")
    private Integer likeCount = 0;

    @Column(name = "freq_count")
    private Integer freqCount = 0;

}