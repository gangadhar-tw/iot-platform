package com.assignment.IoT.platform.service;

import com.assignment.IoT.platform.Exceptions.SensorNotExistException;
import com.assignment.IoT.platform.dto.request.CreateSensorDataRequest;
import com.assignment.IoT.platform.model.SensorData;
import com.assignment.IoT.platform.producer.SensorDataProducerService;
import com.assignment.IoT.platform.repository.SensorDataRepository;
import com.assignment.IoT.platform.repository.SensorRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class SensorDataService {

    private final SensorDataRepository sensorDataRepository;
    private final SensorRepository sensorRepository;
    private final SensorDataProducerService sensorDataProducerService;

    public SensorDataService(SensorDataRepository sensorDataRepository, SensorRepository sensorRepository, SensorDataProducerService sensorDataProducerService) {
        this.sensorDataRepository = sensorDataRepository;
        this.sensorRepository = sensorRepository;
        this.sensorDataProducerService = sensorDataProducerService;
    }

    public SensorData createAndProduceSensorData(CreateSensorDataRequest request) {
        if (!sensorRepository.existsById(request.getSensorId())) {
            throw new SensorNotExistException();
        }

        SensorData sensorData = convertCreateSensorDataRequestToSensorData(request);
        SensorData savedSensorData = sensorDataRepository.save(sensorData);
        sensorDataProducerService.produceSensorData(sensorData);

        return savedSensorData;
    }

    private SensorData convertCreateSensorDataRequestToSensorData(CreateSensorDataRequest request) {
        return SensorData.builder()
                .sensorId(request.getSensorId())
                .temperature(request.getTemperature())
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .batteryPercentage(request.getBatteryPercentage())
                .timestamp(LocalDateTime.now())
                .build();
    }
}
