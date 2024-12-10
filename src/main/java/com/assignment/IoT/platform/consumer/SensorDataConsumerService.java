package com.assignment.IoT.platform.consumer;

import com.assignment.IoT.platform.model.SensorData;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class SensorDataConsumerService {

    private final SensorDataProcessingService processingService;

    public SensorDataConsumerService(SensorDataProcessingService processingService) {
        this.processingService = processingService;
    }

    @KafkaListener(topics = "SENSOR_DATA", groupId = "sensor-data-consumer-group")
    public void consume(SensorData sensorData) {
        processingService.process(sensorData);
    }
}
