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
        status = HttpStatus.NOT_FOUND,
        type = "booking-not-found",
        title = "Booking not found.",
        extensions = {
                @Extension(name = "bookingId", type = Long.class)
        }
)
public class BookingNotFoundException extends AbstractApiException {
    private final Long bookingId;

    public BookingNotFoundException(Long bookingId) {
        super(
                String.format("Booking with id %d couldn't be found.", bookingId)
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
