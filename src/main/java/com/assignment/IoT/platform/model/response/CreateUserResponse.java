package com.assignment.IoT.platform.model.response;

import lombok.Builder;
import lombok.ToString;

import java.util.Set;

@Builder
@ToString
public class CreateUserResponse {
    private String id;
    private String username;
    private String firstName;
    private String lastName;
    private Set<String> roles;
}
