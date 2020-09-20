package com.yanovski.restapi.security.services;

import com.yanovski.restapi.dtos.CreateUserRequest;
import com.yanovski.restapi.models.Role;
import com.yanovski.restapi.models.User;
import com.yanovski.restapi.repositoties.RoleRepository;
import com.yanovski.restapi.repositoties.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class JwtUserDetailsService implements UserDetailsService {

    public static final String ROLE_GUEST = "GUEST";
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder bcryptEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }
        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(),
                new ArrayList<>());
    }

    public User save(CreateUserRequest createUserRequest) {
        User existing = userRepository.findByUsername(createUserRequest.getUsername());
        if(existing != null) {
            throw new AuthenticationServiceException("User exists.");
        }

        User newUser = new User();
        newUser.setUsername(createUserRequest.getUsername());
        newUser.setPassword(bcryptEncoder.encode(createUserRequest.getPassword()));
        Role role;
        if (createUserRequest.getRole() != null) {
            role = roleRepository.findByRoleName(createUserRequest.getRole());
        } else {
            role = roleRepository.findByRoleName(ROLE_GUEST);
        }
        newUser.setRole(role);
        return  userRepository.save(newUser);
    }
}
