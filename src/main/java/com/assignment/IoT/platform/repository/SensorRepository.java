package com.assignment.IoT.platform.repository;

import com.assignment.IoT.platform.model.Sensor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SensorRepository extends JpaRepository<Sensor, Integer> {

}
