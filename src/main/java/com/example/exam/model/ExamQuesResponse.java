package com.example.exam.model;

import com.example.qa.model.QuesResponse;
import lombok.*;

import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExamQuesResponse {
    private ExamResponse examResponse;
    private List<QuesResponse> questions = new ArrayList<>();
}