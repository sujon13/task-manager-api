package com.example.auth.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserOfficeResponse {
    private int userId;

    private int designationId;
    private String designation;
    private String designationFullName;

    private int officeId;
    private String office;
    private String officeFullName;
    private String company;
}
