package com.yanovski.restapi.services;

import com.yanovski.restapi.controllers.payload.CreateUserRequest;
import com.yanovski.restapi.controllers.payload.CreateUserResponse;
import com.yanovski.restapi.security.models.JwtRequest;
import com.yanovski.restapi.security.models.JwtResponse;

public interface UserService {
    JwtResponse createAuthenticationToken(JwtRequest authenticationRequest);
    CreateUserResponse save(CreateUserRequest createUserRequest);
}
