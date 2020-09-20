package com.yanovski.restapi.controllers;

import com.yanovski.restapi.dtos.RoleDTO;
import com.yanovski.restapi.services.RoleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class RoleController {
    @Autowired
    private RoleService roleService;

    @PostMapping("/role")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<Void> create(@RequestBody RoleDTO role) {
        log.info("Creating new role: {}", role);
        roleService.save(role);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
