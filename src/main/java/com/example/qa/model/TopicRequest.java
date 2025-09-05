package com.example.qa.model;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
public class TopicRequest {
    private Integer id;

    private Integer parentId;
    private String engName;
    private String bngName;
}