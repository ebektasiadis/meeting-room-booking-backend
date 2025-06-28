package com.ebektasiadis.meetingroombooking.dto;

import java.time.LocalDateTime;

public record BookingResponse(
        Long id,
        LocalDateTime startTime,
        LocalDateTime endTime,
        String purpose,
        Long userId,
        Long meetingRoomId
) {
}
