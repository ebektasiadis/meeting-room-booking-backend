package com.ebektasiadis.meetingroombooking.exception.booking;

import com.ebektasiadis.meetingroombooking.constants.ProblemTypes;
import com.ebektasiadis.meetingroombooking.exception.common.AbstractApiException;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.HashMap;
import java.util.Map;

@Getter
@ResponseStatus(HttpStatus.NOT_FOUND)
public class BookingNotFoundException extends AbstractApiException {
    private final Long bookingId;

    public BookingNotFoundException(Long bookingId) {
        super(
                String.format("Booking with id %d couldn't be found.", bookingId),
                "Booking not found.",
                ProblemTypes.BOOKING_NOT_FOUND_ERROR
        );

        this.bookingId = bookingId;
    }

    @Override
    public Map<String, Object> getProblemDetailProperties() {
        Map<String, Object> properties = new HashMap<>();
        properties.put("bookingId", this.bookingId);

        return properties;
    }
}
