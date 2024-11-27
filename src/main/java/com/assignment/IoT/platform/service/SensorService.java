package com.assignment.IoT.platform.service;

import com.assignment.IoT.platform.model.Sensor;
import com.assignment.IoT.platform.repository.SensorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SensorService {

    private final SensorRepository sensorRepository;

    @Autowired
    public SensorService(SensorRepository sensorRepository) {
        this.sensorRepository = sensorRepository;
    }

    public List<Sensor> getAllSensors() {
        return sensorRepository.findAll();
    }

    public Optional<Sensor> getSensorById(String id) {
        return sensorRepository.findById(id);
    }

    public Sensor createSensor(Sensor sensor) {
        return sensorRepository.save(sensor);
    }

    public boolean deleteSensor(String id) {
        return sensorRepository.findById(id)
                .map(sensor -> {
                    sensorRepository.delete(sensor);
                    return true;
                })
                .orElse(false);
    }

    public Optional<Sensor> updateSensor(String id, Sensor updatedSensor) {
        return sensorRepository.findById(id)
                .map(existingSensor -> {
                    existingSensor.setName(updatedSensor.getName() != null ? updatedSensor.getName() : existingSensor.getName());
                    existingSensor.setTemperature(updatedSensor.getTemperature() != null ? updatedSensor.getTemperature() : existingSensor.getTemperature());
                    existingSensor.setLongitude(updatedSensor.getLongitude() != null ? updatedSensor.getLongitude() : existingSensor.getLongitude());
                    existingSensor.setLatitude(updatedSensor.getLatitude() != null ? updatedSensor.getLatitude() : existingSensor.getLatitude());
                    return sensorRepository.save(existingSensor);
                });
    }
}
