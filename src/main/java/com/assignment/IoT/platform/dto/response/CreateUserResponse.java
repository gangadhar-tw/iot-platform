package com.assignment.IoT.platform.dto.response;

import lombok.*;

import java.util.Set;

@Builder
@ToString
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateUserResponse {
    private String id;
    private String username;
    private String firstName;
    private String lastName;
    private Set<String> roles;
}
