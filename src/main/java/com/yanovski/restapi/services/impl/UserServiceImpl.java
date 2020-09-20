package com.yanovski.restapi.services.impl;

import com.yanovski.restapi.controllers.payload.CreateUserRequest;
import com.yanovski.restapi.controllers.payload.CreateUserResponse;
import com.yanovski.restapi.dtos.UserDTO;
import com.yanovski.restapi.models.Role;
import com.yanovski.restapi.models.User;
import com.yanovski.restapi.repositoties.RoleRepository;
import com.yanovski.restapi.repositoties.UserRepository;
import com.yanovski.restapi.security.config.JwtTokenUtil;
import com.yanovski.restapi.security.models.JwtRequest;
import com.yanovski.restapi.security.models.JwtResponse;
import com.yanovski.restapi.security.models.UserDetailsImpl;
import com.yanovski.restapi.services.UserService;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserServiceImpl implements UserService {
    public static final String USER_DISABLED = "USER_DISABLED";
    public static final String INVALID_CREDENTIALS = "INVALID_CREDENTIALS";
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder bcryptEncoder;

    private final ModelMapper mapper = new ModelMapper();

    @Override
    public JwtResponse createAuthenticationToken(JwtRequest authenticationRequest) {
        JwtResponse response = new JwtResponse();
        Authentication authentication;
        try {
            authentication = authenticate(authenticationRequest.getUsername(), authenticationRequest.getPassword());
        } catch (DisabledException e) {
            log.error(USER_DISABLED, e);
            response.getErrors().add(USER_DISABLED);
            return response;
        } catch (BadCredentialsException e) {
            log.error(INVALID_CREDENTIALS, e);
            response.getErrors().add(INVALID_CREDENTIALS);
            return response;
        }

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtTokenUtil.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        Set<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());

        response.setUserId(userDetails.getId());
        response.setToken(jwt);
        response.setUsername(userDetails.getUsername());
        response.setEmail(userDetails.getEmail());
        response.setRoles(roles);
        return response;
    }

    @Override
    public CreateUserResponse save(CreateUserRequest createUserRequest) {
        CreateUserResponse response = new CreateUserResponse();
        if (userRepository.existsByUsername(createUserRequest.getUsername())) {
            response.getErrors().add("Username is already taken!");
            return response;
        }

        if (userRepository.existsByEmail(createUserRequest.getEmail())) {
            response.getErrors().add("Email is already in use!");
            return response;
        }

        User newUser = new User();
        newUser.setUsername(createUserRequest.getUsername());
        newUser.setPassword(bcryptEncoder.encode(createUserRequest.getPassword()));
        newUser.setEmail(createUserRequest.getEmail());
        Set<String> strRoles = createUserRequest.getRoles();
        Set<Role> roles = new HashSet<>();

        if (strRoles == null) {
            Role userRole = roleRepository.findByRoleName(Role.Name.ROLE_USER)
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roles.add(userRole);
        } else {
            strRoles.forEach(role -> {
                switch (role) {
                    case "student":
                        Optional<Role> studentRole = roleRepository.findByRoleName(Role.Name.ROLE_STUDENT);
                        studentRole.ifPresent(roles::add);
                        break;
                    case "teacher":
                        Optional<Role> modRole = roleRepository.findByRoleName(Role.Name.ROLE_TEACHER);
                        modRole.ifPresent(roles::add);
                        break;
                    default:
                        Optional<Role> userRole = roleRepository.findByRoleName(Role.Name.ROLE_USER);
                        userRole.ifPresent(roles::add);
                }
            });
        }
        newUser.getRoles().addAll(roles);
        User persisted = userRepository.save(newUser);
        response.setUser(mapper.map(persisted, UserDTO.class));
        return  response;
    }

    private Authentication authenticate(String username, String password) {
        return authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
    }
}
