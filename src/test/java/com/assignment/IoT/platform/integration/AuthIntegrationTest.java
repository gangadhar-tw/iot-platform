package com.assignment.IoT.platform.integration;

import com.assignment.IoT.platform.auth.JwtUtil;
import com.assignment.IoT.platform.dto.request.CreateUserRequest;
import com.assignment.IoT.platform.dto.request.LoginRequest;
import com.assignment.IoT.platform.model.User;
import com.assignment.IoT.platform.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDateTime;
import java.util.Set;

import static com.assignment.IoT.platform.integration.SensorIntegrationTest.mongoDBContainer;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Testcontainers
public class AuthIntegrationTest {

    @Container
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:6.0.6");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    void shouldRegisterUserWhenValidDataIsGiven() throws Exception {
        CreateUserRequest request = new CreateUserRequest("newUser", "password123", "New", "User");

        mockMvc.perform(post("/iot/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("newUser"))
                .andExpect(jsonPath("$.firstName").value("New"))
                .andExpect(jsonPath("$.lastName").value("User"));
    }

    @Test
    void shouldReturnConflictWhenRegisterUserFailsDueToExistingUsername() throws Exception {
        User existingUser = getUser("existingUser", "password123");
        userRepository.save(existingUser);

        CreateUserRequest request = new CreateUserRequest("existingUser", "password123", "Existing", "User");

        mockMvc.perform(post("/iot/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }

    @Test
    void shouldReturnBadRequestWhenRegisterUserFailsDueToValidation() throws Exception {
        CreateUserRequest invalidRequest = new CreateUserRequest("", "short", "", "User");

        mockMvc.perform(post("/iot/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnOkWhenLoginIsSuccessful() throws Exception {
        User user = getUser("validUser", "password123");
        userRepository.save(user);

        LoginRequest request = new LoginRequest("validUser", "password123");

        mockMvc.perform(post("/iot/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.username").value("validUser"));
    }

    @Test
    void shouldReturnUnauthorizedWhenLoginFailsDueToInvalidCredentials() throws Exception {
        User user = getUser("validUser", "password123");
        userRepository.save(user);

        LoginRequest request = new LoginRequest("validUser", "wrongPassword");

        mockMvc.perform(post("/iot/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
    }


    private User getUser(String username, String password) {
        return User.builder()
                .username(username)
                .passwordEncrypted(passwordEncoder.encode(password))
                .firstName("firstName")
                .lastName("lastName")
                .roles(Set.of("ROLE_USER"))
                .createdAt(LocalDateTime.now())
                .build();
    }
}
