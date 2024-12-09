package com.assignment.IoT.platform.model;

import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "sensorData")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SensorData {

    @Id
    private String id;

    @NotBlank(message = "Sensor Id is must for sensorData")
    private String sensorId;

    @NotNull(message = "Temperature is required")
    @Min(value = -90, message = "Temperature cannot be less than -90°C")
    @Max(value = 70, message = "Temperature cannot exceed 70°C")
    private Double temperature;

    @NotNull(message = "Latitude is required")
    @DecimalMin(value = "-90.0", inclusive = true, message = "Latitude must be between -90 and 90")
    @DecimalMax(value = "90.0", inclusive = true, message = "Latitude must be between -90 and 90")
    private Double latitude;

    @NotNull(message = "Longitude is required")
    @DecimalMin(value = "-180.0", inclusive = true, message = "Longitude must be between -180 and 180")
    @DecimalMax(value = "180.0", inclusive = true, message = "Longitude must be between -180 and 180")
    private Double longitude;

    @NotNull(message = "Battery percentage is required")
    @DecimalMin(value = "0", inclusive = true, message = "Battery percentage cannot be lesser than 0")
    @DecimalMax(value = "100", inclusive = true, message = "Battery percentage cannot be greater than 100")
    private Double batteryPercentage;

    private LocalDateTime timestamp;
}
