package com.assignment.IoT.platform.controller;

import com.assignment.IoT.platform.model.Sensor;
import com.assignment.IoT.platform.repository.SensorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/iot/sensors")
public class SensorController {

    private final SensorRepository sensorRepository;

    @Autowired
    public SensorController(SensorRepository sensorRepository) {
        this.sensorRepository = sensorRepository;
    }

    @GetMapping
    public List<Sensor> getAllSensors() {
        return sensorRepository.findAll();
    }

    @GetMapping("/{id}")
    public Sensor getSensorById(@PathVariable String id) {
        return sensorRepository.findById(id).orElse(null);
    }

    @PostMapping
    public String createSensor(@RequestBody Sensor sensor) {
        sensorRepository.save(sensor);
        return "sensor created";
    }

    @DeleteMapping("/{id}")
    public String deleteSensor(@PathVariable String id) {
        sensorRepository.deleteById(id);
        return "sensor with id : " + id + " deleted successfully";
    }

    @PutMapping("/{id}")
    public String updateSensor(@PathVariable String id, @RequestBody Sensor sensor) {
        sensorRepository.save(sensor);
        return "sensor with id: " + id + " updated";
    }

}