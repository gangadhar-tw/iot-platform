package com.assignment.IoT.platform.controller;

import com.assignment.IoT.platform.dto.request.CreateSensorDataRequest;
import com.assignment.IoT.platform.model.SensorData;
import com.assignment.IoT.platform.service.SensorDataService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/iot/producer")
public class SensorDataController {

    private final SensorDataService sensorDataService;

    public SensorDataController(SensorDataService sensorDataService) {
        this.sensorDataService = sensorDataService;
    }

    @PostMapping("/message")
    public ResponseEntity<SensorData> createAndProduceSensorData(@Valid @RequestBody CreateSensorDataRequest createSensorDataRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(sensorDataService.createAndProduceSensorData(createSensorDataRequest));
    }
}
