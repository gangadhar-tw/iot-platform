package com.assignment.IoT.platform.controller;

import com.assignment.IoT.platform.model.Sensor;
import com.assignment.IoT.platform.service.SensorService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/iot/sensors")
public class SensorController {

    private final SensorService sensorService;

    @Autowired
    public SensorController(SensorService sensorService) {
        this.sensorService = sensorService;
    }

    @GetMapping
    public ResponseEntity<List<Sensor>> getAllSensors() {
        return ResponseEntity.ok(sensorService.getAllSensors());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Sensor> getSensorById(@PathVariable String id) {
        return sensorService.getSensorById(id)
                .map(sensor -> ResponseEntity.ok(sensor))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Sensor> createSensor(@Valid @RequestBody Sensor sensor) {
        return ResponseEntity.status(HttpStatus.CREATED).body(sensorService.createSensor(sensor));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSensor(@PathVariable String id) {
        boolean isDeleted = sensorService.deleteSensor(id);
        return isDeleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Sensor> updateSensor(@PathVariable String id, @RequestBody Sensor updatedSensor) {
        return sensorService.updateSensor(id, updatedSensor)
                .map(sensor -> ResponseEntity.ok(sensor))
                .orElse(ResponseEntity.notFound().build());
    }
}