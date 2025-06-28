package com.ebektasiadis.meetingroombooking.exception.booking;

import com.ebektasiadis.meetingroombooking.constants.ProblemTypes;
import com.ebektasiadis.meetingroombooking.exception.common.AbstractApiException;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.HashMap;
import java.util.Map;

@Getter
@ResponseStatus(HttpStatus.CONFLICT)
public class BookingDateConflictException extends AbstractApiException {
    private final Long meetingRoomId;

    public BookingDateConflictException(Long meetingRoomId) {
        super(
                String.format("Meeting room %d is already booked for the timeslot given.", meetingRoomId),
                "Meeting room is already booked for the requested time slot.",
                ProblemTypes.BOOKING_DATE_CONFLICT_ERROR
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
