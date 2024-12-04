package com.assignment.IoT.platform.integration;

import com.assignment.IoT.platform.auth.JwtUtil;
import com.assignment.IoT.platform.model.Sensor;
import com.assignment.IoT.platform.model.User;
import com.assignment.IoT.platform.repository.SensorRepository;
import com.assignment.IoT.platform.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.springframework.security.authentication.AuthenticationManager;

import java.time.LocalDateTime;
import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Testcontainers
public class SensorIntegrationTest {
    @Container
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:6.0.6");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private SensorRepository sensorRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }

    private Sensor sensor;
    private String token;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        sensorRepository.deleteAll();
        sensor = Sensor.builder()
                .name("test-sensor")
                .temperature(24.23)
                .longitude(34.5234)
                .latitude(12.5721)
                .build();
        User user = User.builder()
                .username("default-admin")
                .passwordEncrypted(passwordEncoder.encode("admin@123"))
                .firstName("Admin")
                .lastName("1")
                .roles(Set.of("ROLE_ADMIN"))
                .createdAt(LocalDateTime.now())
                .build();
        userRepository.save(user);
        token = jwtUtil.generateToken(user.getUsername());
    }

    @Test
    void shouldAddSensor() throws Exception {
        mockMvc.perform(post("/iot/sensors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sensor))
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isCreated());
    }
}
