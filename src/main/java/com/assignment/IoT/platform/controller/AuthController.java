package com.assignment.IoT.platform.controller;

import com.assignment.IoT.platform.Exceptions.InvalidCredentialsException;
import com.assignment.IoT.platform.Exceptions.UserNameAlreadyExistException;
import com.assignment.IoT.platform.dto.request.CreateUserRequest;
import com.assignment.IoT.platform.dto.request.LoginRequest;
import com.assignment.IoT.platform.dto.response.CreateUserResponse;
import com.assignment.IoT.platform.dto.response.LoginResponse;
import com.assignment.IoT.platform.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/iot/auth")
public class AuthController {
    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<CreateUserResponse> registerUser(@Valid @RequestBody CreateUserRequest createUserRequest) {
        try {
            CreateUserResponse registeredUser = userService.save(createUserRequest);
            return new ResponseEntity<>(registeredUser, HttpStatus.CREATED);
        } catch (UserNameAlreadyExistException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> loginUser(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            String token = userService.login(loginRequest);
            return ResponseEntity.ok(LoginResponse.builder().token(token).username(loginRequest.getUsername()).build());
        } catch (InvalidCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
}
