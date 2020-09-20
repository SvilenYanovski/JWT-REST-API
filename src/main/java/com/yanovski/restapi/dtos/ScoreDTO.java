package com.yanovski.restapi.dtos;

import lombok.Data;

import java.time.LocalDate;

@Data
public class ScoreDTO {
    private Double score;
    private LocalDate date;
    private UserDTO userDTO;
    private CourseDTO courseDTO;
}
