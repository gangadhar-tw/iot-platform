package com.assignment.IoT.platform.consumer;

import com.assignment.IoT.platform.model.SensorData;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class SensorDataConsumerServiceTest {

    @Mock
    private SensorDataProcessingService processingService;

    @InjectMocks
    private SensorDataConsumerService consumerService;

    @Test
    void shouldForwardMessageToProcessingServiceWhenMessageIsConsumed() {
        SensorData sensorData = getSensorData();

        consumerService.consume(sensorData);

        verify(processingService, times(1)).process(sensorData);
    }

    private SensorData getSensorData() {
        return SensorData.builder()
                .sensorId("1")
                .temperature(25.5)
                .latitude(40.7128)
                .longitude(74.0060)
                .batteryPercentage(95.0)
                .timestamp(LocalDateTime.now())
                .build();
    }
}
