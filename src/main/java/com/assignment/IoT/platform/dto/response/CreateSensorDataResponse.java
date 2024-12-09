package com.assignment.IoT.platform.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateSensorDataResponse {
    private String sensorId;
    private Double latitude;
    private Double longitude;
    private Double temperature;
    private Double batteryPercentage;
    private LocalDateTime timestamp;
}
