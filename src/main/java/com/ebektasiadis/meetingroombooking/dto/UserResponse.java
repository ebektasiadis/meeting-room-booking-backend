package com.ebektasiadis.meetingroombooking.dto;

public record UserResponse(
        Long id,
        String username,
        String email
) {
}
