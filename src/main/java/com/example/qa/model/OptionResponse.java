package com.example.qa.model;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
public class OptionResponse extends Auditable {
    private Integer id;
    private Integer questionId;
    private Integer serial;
    private String valueEn;
    private String valueBn;
}