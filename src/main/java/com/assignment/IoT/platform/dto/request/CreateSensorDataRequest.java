package com.assignment.IoT.platform.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateSensorDataRequest {

    @NotBlank(message = "Sensor Id is must for sensorData")
    private String sensorId;

    @NotBlank(message = "Temperature is required")
    @Min(value = -90, message = "Temperature cannot be less than -90°C")
    @Max(value = 70, message = "Temperature cannot exceed 70°C")
    private Double temperature;

    @NotBlank(message = "Latitude is required")
    @DecimalMin(value = "-90.0", inclusive = true, message = "Latitude must be between -90 and 90")
    @DecimalMax(value = "90.0", inclusive = true, message = "Latitude must be between -90 and 90")
    private Double latitude;

    @NotBlank(message = "Longitude is required")
    @DecimalMin(value = "-180.0", inclusive = true, message = "Longitude must be between -180 and 180")
    @DecimalMax(value = "180.0", inclusive = true, message = "Longitude must be between -180 and 180")
    private Double longitude;

    @NotBlank(message = "Battery percentage is required")
    @DecimalMin(value = "0", inclusive = true, message = "Battery percentage cannot be lesser than 0")
    @DecimalMax(value = "100", inclusive = true, message = "Battery percentage cannot be greater than 100")
    private Double batteryPercentage;

    private LocalDateTime timestamp;

}
