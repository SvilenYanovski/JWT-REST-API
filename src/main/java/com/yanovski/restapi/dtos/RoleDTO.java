package com.yanovski.restapi.dtos;

import com.yanovski.restapi.models.Role;
import lombok.Data;

@Data
public class RoleDTO {
    private Role.Name roleName;
}
