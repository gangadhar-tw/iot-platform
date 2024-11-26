package com.assignment.IoT.platform.controller;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/iot/sensors")
public class SensorController {

    @GetMapping
    public String getAllSensors() {
        return "All the sensors";
    }

    @GetMapping("/{id}")
    public String getSensorById(@PathVariable int id) {
        return "Sensor with id : " + id;
    }

    @PostMapping
    public String createSensor() {
        return "sensor created";
    }

    @DeleteMapping("/{id}")
    public String deleteSensor(@PathVariable int id) {
        return "sensor with id : " + id + " deleted successfully";
    }

    @PutMapping("/{id}")
    public String updateSensor(@PathVariable int id) {
        return "sensor with id: " + id + " updated";
    }

}