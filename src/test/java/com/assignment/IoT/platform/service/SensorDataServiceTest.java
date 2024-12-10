package com.assignment.IoT.platform.service;

import com.assignment.IoT.platform.Exceptions.SensorNotExistException;
import com.assignment.IoT.platform.dto.request.CreateSensorDataRequest;
import com.assignment.IoT.platform.model.SensorData;
import com.assignment.IoT.platform.producer.SensorDataProducerService;
import com.assignment.IoT.platform.repository.SensorDataRepository;
import com.assignment.IoT.platform.repository.SensorRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SensorDataServiceTest {

    @InjectMocks
    private SensorDataService sensorDataService;

    @Mock
    private SensorDataRepository sensorDataRepository;

    @Mock
    private SensorRepository sensorRepository;

    @Mock
    private SensorDataProducerService sensorDataProducerService;

    @Test
    void shouldCreateAndProduceTheSensorDataWhenSensorDataWithExistingSensorIdIsGiven() {
        CreateSensorDataRequest request = getCreateSensorDataRequest();
        when(sensorRepository.existsById(request.getSensorId())).thenReturn(true);
        when(sensorDataRepository.save(any(SensorData.class))).thenReturn(getSensorData("1"));

        SensorData savedSensorData = sensorDataService.createAndProduceSensorData(request);

        assertNotNull(savedSensorData);
        assertEquals(request.getSensorId(), savedSensorData.getSensorId());
        verify(sensorRepository, times(1)).existsById(request.getSensorId());
        verify(sensorDataRepository, times(1)).save(any(SensorData.class));
        verify(sensorDataProducerService, times(1)).produceSensorData(any(SensorData.class));

    }

    @Test
    void shouldThrowErrorWhenSensorDataWithNonExistingSensorIdIsGivenToCreateAndProduceSensorDataMethod() {
        when(sensorRepository.existsById(anyString())).thenReturn(false);

        assertThrows(SensorNotExistException.class, () -> sensorDataService.createAndProduceSensorData(getCreateSensorDataRequest()));
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