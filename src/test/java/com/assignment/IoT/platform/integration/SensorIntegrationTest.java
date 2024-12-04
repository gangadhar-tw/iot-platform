package com.assignment.IoT.platform.integration;

import com.assignment.IoT.platform.auth.JwtUtil;
import com.assignment.IoT.platform.model.Sensor;
import com.assignment.IoT.platform.model.User;
import com.assignment.IoT.platform.repository.SensorRepository;
import com.assignment.IoT.platform.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
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

import static org.junit.Assert.assertFalse;
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


    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        sensorRepository.deleteAll();
    }

    @Test
    void shouldAddSensor() throws Exception {
        Sensor sensor = getSensor();
        String token = createAdminAndGenerateToken();
        mockMvc.perform(post("/iot/sensors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sensor))
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isCreated());
    }

    @Test
    void shouldReturnBadRequestWhenAddingInvalidSensor() throws Exception {
        String token = createAdminAndGenerateToken();
        Sensor invalidSensor = Sensor.builder().build();

        mockMvc.perform(post("/iot/sensors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidSensor))
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldFailToAddSensorWithUserRole() throws Exception {
        String userToken = createRegularUserAndGenerateToken();

        mockMvc.perform(post("/iot/sensors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(getSensor()))
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldGetAllSensors() throws Exception {
        String token = createRegularUserAndGenerateToken();
        Sensor sensor = sensorRepository.save(getSensor());

        mockMvc.perform(get("/iot/sensors")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].name").value(sensor.getName()))
                .andExpect(jsonPath("$[0].temperature").value(sensor.getTemperature()));
    }

    @Test
    void shouldGetSensorById() throws Exception {
        String token = createRegularUserAndGenerateToken();
        Sensor sensor = sensorRepository.save(getSensor());

        mockMvc.perform(get("/iot/sensors/{id}", sensor.getId())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(sensor.getName()))
                .andExpect(jsonPath("$.temperature").value(sensor.getTemperature()));
    }

    @Test
    void shouldReturnNotFoundWhenSensorByIdDoesNotExist() throws Exception {
        String token = createAdminAndGenerateToken();

        mockMvc.perform(get("/iot/sensors/{id}", "invalid-id")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldUpdateSensor() throws Exception {
        String token = createAdminAndGenerateToken();
        Sensor sensor = sensorRepository.save(getSensor());
        Sensor updatedSensor = Sensor.builder()
                .name("updated-sensor")
                .temperature(30.00)
                .longitude(40.0000)
                .latitude(20.0000)
                .build();

        mockMvc.perform(put("/iot/sensors/{id}", sensor.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedSensor))
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(updatedSensor.getName()))
                .andExpect(jsonPath("$.temperature").value(updatedSensor.getTemperature()));
    }

    @Test
    void shouldFailToUpdateSensorWithUserRole() throws Exception {
        String userToken = createRegularUserAndGenerateToken();

        Sensor updatedSensor = Sensor.builder()
                .name("updated-sensor")
                .temperature(25.5)
                .longitude(35.1234)
                .latitude(12.6789)
                .build();

        String sensorId = "existing-sensor-id";

        mockMvc.perform(put("/iot/sensors/{id}", sensorId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedSensor))
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldDeleteSensor() throws Exception {
        String token = createAdminAndGenerateToken();
        Sensor sensor = sensorRepository.save(getSensor());

        mockMvc.perform(delete("/iot/sensors/{id}", sensor.getId())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());

        Assertions.assertFalse(sensorRepository.findById(sensor.getId()).isPresent());
    }

    @Test
    void shouldFailToDeleteSensorWithUserRole() throws Exception {
        String userToken = createRegularUserAndGenerateToken();

        String sensorId = "existing-sensor-id";

        mockMvc.perform(delete("/iot/sensors/{id}", sensorId)
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isForbidden());
    }

    private Sensor getSensor() {
        return Sensor.builder()
                .name("test-sensor")
                .temperature(24.23)
                .longitude(34.5234)
                .latitude(12.5721)
                .build();
    }


    private User createUser(String username, String password, String role) {
        User user = User.builder()
                .username(username)
                .passwordEncrypted(passwordEncoder.encode(password))
                .firstName("User")
                .lastName("Test")
                .roles(Set.of(role))
                .createdAt(LocalDateTime.now())
                .build();
        return userRepository.save(user);
    }

    private String generateTokenForUser(User user) {
        return jwtUtil.generateToken(user.getUsername());
    }

    private String createAdminAndGenerateToken() {
        User admin = createUser("default-admin", "admin@123", "ROLE_ADMIN");
        return generateTokenForUser(admin);
    }

    private String createRegularUserAndGenerateToken() {
        User regularUser = createUser("default-user", "user@123", "ROLE_USER");
        return generateTokenForUser(regularUser);
    }


}
