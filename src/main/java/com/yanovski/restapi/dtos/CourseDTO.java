package com.yanovski.restapi.dtos;

import lombok.Data;

@Data
public class CourseDTO {
    private Long id;
    private String courseName;
    private UserDTO teacher;
}
