package com.yanovski.restapi.services.impl;

import com.yanovski.restapi.dtos.RoleDTO;
import com.yanovski.restapi.models.Role;
import com.yanovski.restapi.repositoties.RoleRepository;
import com.yanovski.restapi.services.RoleService;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class RoleServiceImpl implements RoleService {
    @Autowired
    private RoleRepository roleRepository;

    private final Set<RoleDTO> roles = new HashSet<>();

    @Autowired
    private ModelMapper mapper;

    @PostConstruct
    void initRoles() {
        roles.addAll(roleRepository.findAll().stream()
                .map(r -> mapper.map(r, RoleDTO.class))
                .collect(Collectors.toSet()));
    }

    @Override
    public Set<RoleDTO> getRoles() {
        return Collections.unmodifiableSet(roles);
    }

    @Override
    public void save(RoleDTO roleDTO) {
        Role role = roleRepository.save(mapper.map(roleDTO, Role.class));
        roles.add(mapper.map(role, RoleDTO.class));
    }

    @Override
    public Optional<RoleDTO> findByRoleName(String name) {
        return roles.stream().filter(r -> r.getRoleName().name().equals(name)).findFirst();
    }
}
