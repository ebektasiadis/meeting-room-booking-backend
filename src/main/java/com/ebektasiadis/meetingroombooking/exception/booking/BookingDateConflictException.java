package com.ebektasiadis.meetingroombooking.exception.booking;

import com.ebektasiadis.meetingroombooking.exception.common.AbstractApiException;
import com.ebektasiadis.meetingroombooking.exception.common.Extension;
import com.ebektasiadis.meetingroombooking.exception.common.ResponseProblemDetail;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;

@Getter
@ResponseProblemDetail(
        status = HttpStatus.CONFLICT,
        type = "booking-date-conflict",
        title = "Meeting room is already booked for the requested time slot.",
        extensions = {
                @Extension(name = "meetingRoomId", type = Long.class),
        }
)
public class BookingDateConflictException extends AbstractApiException {
    private final Long meetingRoomId;

    public BookingDateConflictException(Long meetingRoomId) {
        super(
                String.format("Meeting room %d is already booked for the timeslot given.", meetingRoomId)
        );

        this.meetingRoomId = meetingRoomId;
    }


    @Override
    public Map<String, Object> getProblemDetailProperties() {
        Map<String, Object> properties = new HashMap<>();
        properties.put("meetingRoomId", meetingRoomId);

        return properties;
    }
}
