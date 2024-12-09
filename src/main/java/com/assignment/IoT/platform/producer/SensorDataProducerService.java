package com.assignment.IoT.platform.producer;

import com.assignment.IoT.platform.dto.request.CreateSensorDataRequest;
import com.assignment.IoT.platform.model.SensorData;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class SensorDataProducerService {
    private final String SENSOR_DATA_TOPIC = "SENSOR_DATA";
    private final KafkaTemplate<String, SensorData> kafkaTemplate;

    public SensorDataProducerService(KafkaTemplate<String, SensorData> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void produceSensorData(SensorData sensorData) {
        kafkaTemplate.send(SENSOR_DATA_TOPIC, sensorData);
    }
}
