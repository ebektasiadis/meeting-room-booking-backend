package com.ebektasiadis.meetingroombooking.dto;

public record MeetingRoomResponse(
        Long id,
        String name,
        Integer capacity,
        String location,
        Boolean hasProjector,
        Boolean hasWhiteboard
) {
}
