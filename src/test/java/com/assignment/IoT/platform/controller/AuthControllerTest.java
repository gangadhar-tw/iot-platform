package com.assignment.IoT.platform.controller;

import com.assignment.IoT.platform.Exceptions.InvalidCredentialsException;
import com.assignment.IoT.platform.Exceptions.UserNameAlreadyExistException;
import com.assignment.IoT.platform.dto.request.CreateUserRequest;
import com.assignment.IoT.platform.dto.request.LoginRequest;
import com.assignment.IoT.platform.dto.response.CreateUserResponse;
import com.assignment.IoT.platform.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Test
    void shouldRegisterUserWhenValidUserIsGiven() throws Exception {
        CreateUserRequest request = new CreateUserRequest("testUser", "password123", "Test", "User");
        CreateUserResponse response = new CreateUserResponse("1", "testUser", "Test", "User", Set.of("ROLE_USER"));

        when(userService.save(any(CreateUserRequest.class))).thenReturn(response);

        mockMvc.perform(post("/iot/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value(response.getUsername()))
                .andExpect(jsonPath("$.firstName").value(response.getFirstName()))
                .andExpect(jsonPath("$.lastName").value(response.getLastName()));

        verify(userService, times(1)).save(any(CreateUserRequest.class));
    }

    @Test
    void shouldReturnBadRequestWhenRegisterUserFailsDueToValidation() throws Exception {
        CreateUserRequest invalidRequest = new CreateUserRequest("", "short", "", "User");

        mockMvc.perform(post("/iot/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnConflictWhenRegisterUserFailsDueToExistingUsername() throws Exception {
        CreateUserRequest request = new CreateUserRequest("testUser", "password123", "Test", "User");

        when(userService.save(request)).thenThrow(UserNameAlreadyExistException.class);

        mockMvc.perform(post("/iot/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(status().isConflict());
    }

    @Test
    void shouldReturnOkWhenLoginIsSuccessful() throws Exception {
        LoginRequest request = new LoginRequest("testUser", "password123");
        String token = "testToken";

        when(userService.login(request)).thenReturn(token);

        mockMvc.perform(post("/iot/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value(token))
                .andExpect(jsonPath("$.username").value(request.getUsername()));
    }

    @Test
    void shouldReturnUnauthorizedWhenLoginFailsDueToInvalidCredentials() throws Exception {
        LoginRequest request = new LoginRequest("testUser", "wrongPassword");

        when(userService.login(request)).thenThrow(InvalidCredentialsException.class);

        mockMvc.perform(post("/iot/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldReturnBadRequestWhenLoginUserFailsDueToValidation() throws Exception {
        LoginRequest loginRequest = new LoginRequest("", "short");

        mockMvc.perform(post("/iot/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(loginRequest)))
                .andExpect(status().isBadRequest());
    }
}