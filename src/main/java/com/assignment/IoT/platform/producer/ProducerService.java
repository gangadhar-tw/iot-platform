package com.assignment.IoT.platform.producer;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class ProducerService {
    private final String SENSOR_DATA_TOPIC = "SENSOR_DATA";
    private final KafkaTemplate<String, String> kafkaTemplate;

    public ProducerService(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void produceSensorData(String message) {
        kafkaTemplate.send(SENSOR_DATA_TOPIC, message);
    }
}
