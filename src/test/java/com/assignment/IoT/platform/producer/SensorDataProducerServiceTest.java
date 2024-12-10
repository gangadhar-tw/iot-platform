package com.assignment.IoT.platform.producer;

import com.assignment.IoT.platform.model.SensorData;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.time.LocalDateTime;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SensorDataProducerServiceTest {

    @Mock
    private KafkaTemplate<String, SensorData> kafkaTemplate;

    @InjectMocks
    private SensorDataProducerService sensorDataProducerService;

    private final String SENSOR_DATA_TOPIC = "SENSOR_DATA";

    @Test
    void shouldSendMessageToKafkaWhenProduceSensorDataIsCalledWithValidSensorData() {
        SensorData sensorData = SensorData.builder()
                .sensorId("1")
                .temperature(25.5)
                .latitude(40.7128)
                .longitude(74.0060)
                .batteryPercentage(95.0)
                .timestamp(LocalDateTime.now())
                .build();

        sensorDataProducerService.produceSensorData(sensorData);

        verify(kafkaTemplate, times(1)).send(SENSOR_DATA_TOPIC, sensorData);
    }

    @Test
    void shouldNotSendMessageIfSensorDataIsNull() {

        sensorDataProducerService.produceSensorData(null);

        verify(kafkaTemplate, never()).send(anyString(), any(SensorData.class));
    }
}