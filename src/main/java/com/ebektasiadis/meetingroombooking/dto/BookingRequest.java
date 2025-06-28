package com.ebektasiadis.meetingroombooking.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record BookingRequest(

        @NotNull(message = "Start time is mandatory")
        @FutureOrPresent(message = "Start date must be either in present or in the future")
        LocalDateTime startTime,

        @NotNull(message = "End time is mandatory")
        @Future(message = "End time must be in the future")
        LocalDateTime endTime,

        @NotEmpty(message = "Purpose is mandatory")
        String purpose,

        @NotNull(message = "User ID is mandatory")
        Long userId,

        @NotNull(message = "Meeting room ID is mandatory")
        Long meetingRoomId
) {
}
