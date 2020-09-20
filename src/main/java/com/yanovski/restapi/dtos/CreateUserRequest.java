package com.yanovski.restapi.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateUserRequest {
    @NonNull
    private String username;
    @NonNull
    private String password;
    private String role;
}
