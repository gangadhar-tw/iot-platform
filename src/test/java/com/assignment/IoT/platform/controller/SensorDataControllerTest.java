package com.assignment.IoT.platform.controller;

import com.assignment.IoT.platform.Exceptions.SensorNotExistException;
import com.assignment.IoT.platform.dto.request.CreateSensorDataRequest;
import com.assignment.IoT.platform.model.SensorData;
import com.assignment.IoT.platform.service.SensorDataService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
class SensorDataControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SensorDataService sensorDataService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldReturnCreatedResponseWhenValidSensorDataIsGivenToCreateAndProduce() throws Exception {
        CreateSensorDataRequest request = getCreateSensorDataRequest();
        SensorData sensorData = getSensorData("1");
        when(sensorDataService.createAndProduceSensorData(request)).thenReturn(sensorData);

        mockMvc.perform(post("/iot/producer/message")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isCreated());
        verify(sensorDataService, times(1)).createAndProduceSensorData(request);
    }

    @Test
    void shouldReturnBadRequestWhenInvalidSensorDataIsGivenToCreateAndProduce() throws Exception {
        CreateSensorDataRequest request = new CreateSensorDataRequest();

        mockMvc.perform(post("/iot/producer/message")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(sensorDataService, never()).createAndProduceSensorData(any(CreateSensorDataRequest.class));
    }

    @Test
    void shouldReturnSensorNotExistExceptionWhenSensorDataWithNonExistingSensorIdIsGiven() throws Exception {
        when(sensorDataService.createAndProduceSensorData(any(CreateSensorDataRequest.class))).thenThrow(new SensorNotExistException());

        mockMvc.perform(post("/iot/producer/message")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(getCreateSensorDataRequest())))
                .andExpect(status().isNotFound());
    }

    private SensorData getSensorData(String id) {
        return SensorData.builder()
                .sensorId(id)
                .temperature(25.5)
                .latitude(40.7128)
                .longitude(74.0060)
                .batteryPercentage(95.0)
                .timestamp(LocalDateTime.now())
                .build();
    }

    private CreateSensorDataRequest getCreateSensorDataRequest() {
        return CreateSensorDataRequest.builder()
                .sensorId("1")
                .temperature(25.5)
                .latitude(40.7128)
                .longitude(74.0060)
                .batteryPercentage(95.0)
                .build();
    }
}