package com.example.auth.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UserResponse {
    private int userId;
    private String userName;
    private String name;
    private String email;
}
