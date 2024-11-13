package com.example.qa.model;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TopicRequest {
    private Integer id;

    private Integer parentId;
    private String engName;
    private String bngName;
}