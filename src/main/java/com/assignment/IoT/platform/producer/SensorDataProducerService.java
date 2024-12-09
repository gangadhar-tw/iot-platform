package com.assignment.IoT.platform.producer;

import com.assignment.IoT.platform.dto.request.CreateSensorDataRequest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class SensorDataProducerService {
    private final String SENSOR_DATA_TOPIC = "SENSOR_DATA";
    private final KafkaTemplate<String, CreateSensorDataRequest> kafkaTemplate;

        public SensorDataProducerService(KafkaTemplate<String, CreateSensorDataRequest> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void produceSensorData(CreateSensorDataRequest request) {
        kafkaTemplate.send(SENSOR_DATA_TOPIC, request);
    }
}
