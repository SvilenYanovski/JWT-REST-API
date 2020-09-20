package com.yanovski.restapi.dtos;

import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Data
public class UserDTO {
    private Long id;
    private String username;
    private String email;
    private Set<RoleDTO> roles = new HashSet<>();
    private Set<CourseDTO> courses = new HashSet<>();
}
