package com.ebektasiadis.meetingroombooking.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record MeetingRoomRequest(
        @NotBlank(message = "Meeting room name is mandatory")
        String name,

        @NotNull(message = "Capacity is mandatory")
        @Min(value = 1, message = "Capacity must be at least 1")
        Integer capacity,

        @NotBlank(message = "Location is mandatory")
        String location,

        @NotNull(message = "Has projector status is mandatory")
        Boolean hasProjector,

        @NotNull(message = "Has whiteboard status is mandatory")
        Boolean hasWhiteboard
) {
}
