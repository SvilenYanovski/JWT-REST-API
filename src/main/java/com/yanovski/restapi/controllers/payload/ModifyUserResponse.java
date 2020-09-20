package com.yanovski.restapi.controllers.payload;

import com.yanovski.restapi.dtos.UserDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ModifyUserResponse {
    private UserDTO user;
    private Set<String> errors = new HashSet<>();
}
