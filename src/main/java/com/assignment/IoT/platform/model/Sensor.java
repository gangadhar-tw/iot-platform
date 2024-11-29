package com.assignment.IoT.platform.model;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "sensors")
@Builder
public class Sensor {
    @Id
    private String id;

    @NotBlank(message = "Name cannot be blank")
    private String name;

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

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}