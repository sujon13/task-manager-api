package com.example.qa.model;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OptionResponse extends Auditable {
    private Integer id;
    private Integer questionId;
    private Integer serial;
    private String valueEn;
    private String valueBn;
}