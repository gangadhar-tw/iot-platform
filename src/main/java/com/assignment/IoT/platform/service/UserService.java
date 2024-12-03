package com.assignment.IoT.platform.service;

import com.assignment.IoT.platform.Exceptions.UserNameAlreadyExistException;
import com.assignment.IoT.platform.Exceptions.UserNameNotFoundException;
import com.assignment.IoT.platform.auth.JwtUtil;
import com.assignment.IoT.platform.model.User;
import com.assignment.IoT.platform.dto.request.CreateUserRequest;
import com.assignment.IoT.platform.dto.request.LoginRequest;
import com.assignment.IoT.platform.dto.response.CreateUserResponse;
import com.assignment.IoT.platform.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Component
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtService;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, JwtUtil jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    public CreateUserResponse save(CreateUserRequest request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new UserNameAlreadyExistException();
        }

        User user = convertUserRequestToUser(request);
        User savedUser = userRepository.save(user);
        return CreateUserResponse.builder()
                .id(savedUser.getId())
                .username(savedUser.getUsername())
                .firstName(savedUser.getFirstName())
                .lastName(savedUser.getLastName())
                .roles(savedUser.getRoles())
                .build();
    }

    public String login(LoginRequest loginRequest) {
        String username = loginRequest.getUsername();
        String password = loginRequest.getPassword();
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        if (authentication.isAuthenticated()) {
            return jwtService.generateToken(username);
        } else {
            throw new UserNameNotFoundException();
        }
    }

    private User convertUserRequestToUser(CreateUserRequest request) {
        User user = new User();
        user.setUsername(request.getUsername());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setPasswordEncrypted(passwordEncoder.encode(request.getPassword()));
        user.setCreatedAt(LocalDateTime.now());
        Set<String> roles = new HashSet<>();
        roles.add("ROLE_USER");
        user.setRoles(roles);
        return user;
    }
}
