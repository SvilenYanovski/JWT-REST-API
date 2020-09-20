package com.yanovski.restapi.services;

import com.yanovski.restapi.controllers.payload.CreateUserRequest;
import com.yanovski.restapi.controllers.payload.EditUserRequest;
import com.yanovski.restapi.controllers.payload.ModifyUserResponse;
import com.yanovski.restapi.security.models.JwtRequest;
import com.yanovski.restapi.security.models.JwtResponse;

import java.util.concurrent.CompletableFuture;

public interface UserService {
    JwtResponse createAuthenticationToken(JwtRequest authenticationRequest);
    ModifyUserResponse save(CreateUserRequest createUserRequest);

    ModifyUserResponse update(EditUserRequest user);

    CompletableFuture<Void> delete(String userName);
}
