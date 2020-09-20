package com.yanovski.restapi.services;

import com.yanovski.restapi.dtos.RoleDTO;

import java.util.Optional;
import java.util.Set;

public interface RoleService {
    Set<RoleDTO> getRoles();
    void save(RoleDTO roleDTO);

    Optional<RoleDTO> findByRoleName(String name);
}
