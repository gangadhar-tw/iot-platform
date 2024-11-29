package com.assignment.IoT.platform.controller;

import com.assignment.IoT.platform.model.Sensor;
import com.assignment.IoT.platform.service.SensorService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(SensorController.class)
class SensorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SensorService sensorService;

    @Test
    void shouldReturnListOfSensorsWhenGetAllSensorsIsCalled() throws Exception {
        List<Sensor> sensors = getSensors();
        when(sensorService.getAllSensors()).thenReturn(sensors);

        mockMvc.perform(get("/iot/sensors"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(sensors.size()));
    }

    @Test
    void shouldReturnSensorWhenGetSensorByIdIsCalledWithValidId() throws Exception {
        Sensor sensor = getSensor();
        when(sensorService.getSensorById("1")).thenReturn(Optional.of(sensor));

        mockMvc.perform(get("/iot/sensors/{id}", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(sensor.getName()))
                .andExpect(jsonPath("$.id").value(sensor.getId()));
    }

    @Test
    void shouldReturnNotFoundWhenGetSensorByIdIsCalledWithInvalidId() throws Exception {
        when(sensorService.getSensorById("99")).thenReturn(Optional.empty());

        mockMvc.perform(get("/iot/sensors/{id}", "99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldCreateSensorWhenValidDataIsProvided() throws Exception {
        Sensor sensor = getSensor();
        when(sensorService.createSensor(sensor)).thenReturn(sensor);

        mockMvc.perform(post("/iot/sensors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(sensor)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value(sensor.getName()))
                .andExpect(jsonPath("$.temperature").value(sensor.getTemperature()));

    }

    @Test
    void shouldReturnBadRequestWhenCreatingSensorWithInvalidData() throws Exception {
        Sensor sensor = new Sensor();

        mockMvc.perform(post("/iot/sensors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(sensor)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldDeleteSensorWhenValidIdIsProvided() throws Exception {
        when(sensorService.deleteSensor("1")).thenReturn(true);

        mockMvc.perform(delete("/iot/sensors/{id}", "1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldReturnNotFoundWhenDeletingSensorWithInvalidId() throws Exception {
        when(sensorService.deleteSensor("99")).thenReturn(false);

        mockMvc.perform(delete("/iot/sensors/{id}", "99"))
                .andExpect(status().isNotFound());

    }

    @Test
    void shouldUpdateSensorWhenValidDataIsProvided() throws Exception {
        Sensor updatedSensor = Sensor.builder().name("updated sensor").build();
        when(sensorService.updateSensor("1", updatedSensor)).thenReturn(Optional.of(updatedSensor));

        mockMvc.perform(put("/iot/sensors/{id}", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(updatedSensor)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(updatedSensor.getName()));

    }

    @Test
    void shouldReturnNotFoundWhenUpdatingSensorWithInvalidId() throws Exception {
        Sensor updatedSensor = Sensor.builder().name("updated sensor").build();
        when(sensorService.updateSensor("1", updatedSensor)).thenReturn(Optional.of(updatedSensor));

        mockMvc.perform(put("/iot/sensors/{id}", "99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(updatedSensor)))
                .andExpect(status().isNotFound());

    }

    private Sensor getSensor() {
        return Sensor.builder().id("1").name("Sensor 1").temperature(23.42).latitude(34.6548).longitude(165.3291).build();
    }

    private List<Sensor> getSensors() {
        return Arrays.asList(getSensor(), Sensor.builder().id("2").name("Sensor 2").temperature(18.5).latitude(-4.1234).longitude(56.8344).build());
    }
}