package com.ebektasiadis.meetingroombooking.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;

public record UserRequest(
        @NotEmpty(message = "Username is mandatory")
        String username,

        @NotEmpty(message = "Email is mandatory")
        @Email(message = "Email should have a valid format")
        String email
) {
}