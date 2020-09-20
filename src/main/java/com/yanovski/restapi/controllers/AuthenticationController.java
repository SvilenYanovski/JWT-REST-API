package com.yanovski.restapi.controllers;

import com.yanovski.restapi.controllers.payload.CreateUserRequest;
import com.yanovski.restapi.controllers.payload.CreateUserResponse;
import com.yanovski.restapi.security.models.JwtRequest;
import com.yanovski.restapi.security.models.JwtResponse;
import com.yanovski.restapi.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin
public class AuthenticationController {
    @Autowired
    private UserService userService;


    @PostMapping("/authenticate")
    public ResponseEntity<JwtResponse> createAuthenticationToken(@RequestBody JwtRequest authenticationRequest) {
        return new ResponseEntity<>(userService.createAuthenticationToken(authenticationRequest), HttpStatus.OK);
    }

    @PostMapping("/register")
    public ResponseEntity<CreateUserResponse> saveUser(@RequestBody CreateUserRequest user) {
        try {
            return new ResponseEntity<>(userService.save(user), HttpStatus.OK);
        } catch (AuthenticationServiceException ex) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}
