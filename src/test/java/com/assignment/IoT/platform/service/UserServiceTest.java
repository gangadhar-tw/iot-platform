package com.assignment.IoT.platform.service;

import com.assignment.IoT.platform.Exceptions.InvalidCredentialsException;
import com.assignment.IoT.platform.Exceptions.UserNameAlreadyExistException;
import com.assignment.IoT.platform.auth.JwtUtil;
import com.assignment.IoT.platform.dto.request.CreateUserRequest;
import com.assignment.IoT.platform.dto.request.LoginRequest;
import com.assignment.IoT.platform.dto.response.CreateUserResponse;
import com.assignment.IoT.platform.model.User;
import com.assignment.IoT.platform.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtUtil jwtUtil;

    @Test
    void shouldReturnCreateUserResponseWhenSaveMethodIsCalledWithCreateUserRequestWithNonExistingUsername() {
        CreateUserRequest createUserRequest = getCreateUserRequest();
        User user = getUser();
        when(userRepository.findByUsername(createUserRequest.getUsername())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(createUserRequest.getPassword())).thenReturn("encryptedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);

        CreateUserResponse response = userService.save(createUserRequest);

        assertNotNull(response);
        assertEquals(createUserRequest.getUsername(), response.getUsername());
        assertEquals(createUserRequest.getFirstName(), response.getFirstName());
        assertEquals(createUserRequest.getLastName(), response.getLastName());
        assertEquals(user.getRoles(), response.getRoles());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void shouldThrowUsernameAlreadyExistExceptionWhenSaveMethodIsCalledWithCreateUserRequestWithExistingUsername() {
        CreateUserRequest createUserRequest = getCreateUserRequest();
        when(userRepository.findByUsername(createUserRequest.getUsername())).thenReturn(Optional.of(getUser()));

        assertThrows(UserNameAlreadyExistException.class, () -> userService.save(createUserRequest));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void shouldReturnJwtTokenWhenLoginMethodIsCalledWithValidCredentials() {
        LoginRequest request = new LoginRequest("testUser", "testPassword");
        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any(Authentication.class))).thenReturn(authentication);
        when(jwtUtil.generateToken(any())).thenReturn("mockJwtToken");

        String token = userService.login(request);

        assertNotNull(token);
        assertEquals("mockJwtToken", token);
        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtUtil, times(1)).generateToken(request.getUsername());
    }

    @Test
    void shouldThrowInvalidCredentialsExceptionWhenLoginMethodIsCalledWithInvalidCredentials() {
        LoginRequest request = new LoginRequest("testUser", "testWrongPassword");
        when(authenticationManager.authenticate(any(Authentication.class))).thenThrow(BadCredentialsException.class);

        assertThrows(InvalidCredentialsException.class, () -> userService.login(request));
        verify(jwtUtil, never()).generateToken(anyString());
    }

    private User getUser() {
        return User.builder()
                .id("1")
                .username("testUser")
                .passwordEncrypted("encryptedPassword")
                .firstName("test")
                .lastName("user")
                .roles(Set.of("ROLE_USER"))
                .createdAt(LocalDateTime.now())
                .build();
    }

    private CreateUserRequest getCreateUserRequest() {
        return CreateUserRequest.builder()
                .username("testUser")
                .password("testPassword")
                .firstName("test")
                .lastName("user")
                .build();
    }


}