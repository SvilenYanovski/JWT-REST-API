package com.yanovski.restapi.security.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JwtResponse {
    private String token;
    private Long userId;
    private String username;
    private String email;
    private Set<String> roles;
    private Set<String> errors = new HashSet<>();
}
