package com.assignment.IoT.platform.repository;

import com.assignment.IoT.platform.model.Sensor;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SensorRepository extends MongoRepository<Sensor, String> {

}
