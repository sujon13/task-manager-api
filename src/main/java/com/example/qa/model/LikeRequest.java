package com.example.qa.model;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LikeRequest {
    private Integer id;
    private Integer type;
    private Integer parentId;
}