package com.assignment.IoT.platform.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {
    @Size(min = 5, max = 20, message = "Username length should be between 5 and 20 characters")
    @NotBlank
    private String username;
    @NotBlank
    @Size(min = 8, max = 20, message = "Password length should be between 8 and 20 characters")
    private String password;
}
