package com.yanovski.restapi.controllers;

import com.yanovski.restapi.controllers.payload.CreateUserRequest;
import com.yanovski.restapi.controllers.payload.EditUserRequest;
import com.yanovski.restapi.controllers.payload.ModifyUserResponse;
import com.yanovski.restapi.dtos.UserDTO;
import com.yanovski.restapi.security.models.JwtRequest;
import com.yanovski.restapi.security.models.JwtResponse;
import com.yanovski.restapi.services.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@CrossOrigin
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/authenticate")
    public ResponseEntity<JwtResponse> createAuthenticationToken(@RequestBody JwtRequest authenticationRequest) {
        return new ResponseEntity<>(userService.createAuthenticationToken(authenticationRequest), HttpStatus.OK);
    }

    @PostMapping("/register")
    public ResponseEntity<ModifyUserResponse> saveUser(@RequestBody CreateUserRequest user) {
        try {
            return new ResponseEntity<>(userService.save(user), HttpStatus.OK);
        } catch (AuthenticationServiceException ex) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/user/update")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<ModifyUserResponse> updateUser(@RequestBody EditUserRequest user) {
        try {
            return new ResponseEntity<>(userService.update(user), HttpStatus.OK);
        } catch (AuthenticationServiceException ex) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @Async
    @DeleteMapping("/user/{userName}/delete")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<Void> deleteUser(@PathVariable String userName) {
        try {
            log.info("Delete User API started.");
            userService.delete(userName);
            log.info("Returning Http Status code OK (200) before the actual delete.");
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (AuthenticationServiceException ex) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/users/{role}")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<List<UserDTO>> updateUser(@PathVariable String role) {
        try {
            return new ResponseEntity<>(userService.findAllByRole(role), HttpStatus.OK);
        } catch (AuthenticationServiceException ex) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}
