package com.example.exam.model;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubmissionStatistics {
    private int answered;
    private int notAnswered;
    private int correct;
    private int wrong;
}
