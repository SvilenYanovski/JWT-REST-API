package com.yanovski.restapi.services.impl;

import com.yanovski.restapi.controllers.payload.CreateUserRequest;
import com.yanovski.restapi.controllers.payload.EditUserRequest;
import com.yanovski.restapi.controllers.payload.ModifyUserResponse;
import com.yanovski.restapi.dtos.RoleDTO;
import com.yanovski.restapi.dtos.UserDTO;
import com.yanovski.restapi.models.Role;
import com.yanovski.restapi.models.User;
import com.yanovski.restapi.repositoties.UserRepository;
import com.yanovski.restapi.security.config.JwtTokenUtil;
import com.yanovski.restapi.security.models.JwtRequest;
import com.yanovski.restapi.security.models.JwtResponse;
import com.yanovski.restapi.security.models.UserDetailsImpl;
import com.yanovski.restapi.services.RoleService;
import com.yanovski.restapi.services.UserService;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
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
    private RoleService roleService;

    @Autowired
    private PasswordEncoder bcryptEncoder;

    @Autowired
    private ModelMapper mapper;

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
    public ModifyUserResponse save(CreateUserRequest createUserRequest) {
        ModifyUserResponse response = new ModifyUserResponse();
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
        Set<RoleDTO> roles = createRoleSet(strRoles);
        newUser.getRoles().addAll(roles.stream().map(r -> mapper.map(r, Role.class)).collect(Collectors.toSet()));

        User persisted = userRepository.save(newUser);
        response.setUser(mapper.map(persisted, UserDTO.class));
        return  response;
    }

    @Override
    public ModifyUserResponse update(EditUserRequest editUserRequest) {
        ModifyUserResponse response = new ModifyUserResponse();
        User usr = userRepository.findById(editUserRequest.getId())
                .orElseThrow(() -> new EntityNotFoundException("User not found!"));

        if (userRepository.existsByUsername(editUserRequest.getUsername())) {
            response.getErrors().add("Username is already taken!");
            return response;
        } else {
            if (!editUserRequest.getUsername().isEmpty()) {
                usr.setUsername(editUserRequest.getUsername());
            }
        }

        if (userRepository.existsByEmail(editUserRequest.getEmail())) {
            response.getErrors().add("Email is already in use!");
            return response;
        } else {
            if (!editUserRequest.getEmail().isEmpty()) {
                usr.setEmail(editUserRequest.getEmail());
            }
        }

        if (!editUserRequest.getPassword().isEmpty()) {
            usr.setPassword(bcryptEncoder.encode(editUserRequest.getPassword()));
        }

        if (editUserRequest.getRoles() != null && !editUserRequest.getRoles().isEmpty()) {
            Set<String> strRoles = editUserRequest.getRoles();
            Set<RoleDTO> roles = createRoleSet(strRoles);
            usr.setRoles(roles.stream().map(r -> mapper.map(r, Role.class)).collect(Collectors.toSet()));
        }

        User updated = userRepository.save(usr);
        response.setUser(mapper.map(updated, UserDTO.class));
        return response;
    }

    @Async
    @Override
    public CompletableFuture<Void> delete(String userName) {
        Optional<User> toDelete = userRepository.findByUsername(userName);
        
        return CompletableFuture.runAsync(() -> {
            log.info("Starting long delete process.");
            try {
                Thread.sleep(10*1000);
            } catch (InterruptedException e) {
                log.error("Interrupted exception....", e);
            }
            toDelete.ifPresent(user -> userRepository.delete(user));
            log.info("Ending long delete process.");
        });
    }

    @Override
    public List<UserDTO> findAllByRole(String role) {
        Optional<RoleDTO> roledto = roleService.findByRoleName("ROLE_" + role.toUpperCase());
        if (roledto.isPresent()) {
            List<User> users = userRepository.findAllByRole(roledto.get().getRoleName());
            return users.stream().map(u -> mapper.map(u, UserDTO.class)).collect(Collectors.toList());
        }
        return null;
    }

    private Authentication authenticate(String username, String password) {
        return authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
    }

    private Set<RoleDTO> createRoleSet(Set<String> strRoles) {
        Set<RoleDTO> roles = new HashSet<>();
        if (strRoles == null || strRoles.isEmpty()) {
            RoleDTO userRole = roleService.findByRoleName(Role.Name.ROLE_USER.name())
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roles.add(userRole);
        } else {
            strRoles.forEach(role -> {
                switch (role) {
                    case "student":
                        Optional<RoleDTO> studentRole = roleService.findByRoleName(Role.Name.ROLE_STUDENT.name());
                        studentRole.ifPresent(roles::add);
                        break;
                    case "teacher":
                        Optional<RoleDTO> teacherRole = roleService.findByRoleName(Role.Name.ROLE_TEACHER.name());
                        teacherRole.ifPresent(roles::add);
                        break;
                    default:
                        Optional<RoleDTO> userRole = roleService.findByRoleName(Role.Name.ROLE_USER.name());
                        userRole.ifPresent(roles::add);
                }
            });
        }
        return roles;
    }
}
