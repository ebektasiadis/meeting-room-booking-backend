package com.ebektasiadis.meetingroombooking.exception.booking;

import com.ebektasiadis.meetingroombooking.constants.ProblemTypes;
import com.ebektasiadis.meetingroombooking.exception.common.AbstractApiException;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Getter
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class BookingPastStartDateException extends AbstractApiException {
    private final LocalDateTime startDate;

    public BookingPastStartDateException(LocalDateTime startDate) {
        super(
                String.format("The selected start date (%s) is in the past.", startDate),
                "The start date cannot be in the past.",
                ProblemTypes.BOOKING_PAST_START_DATE_ERROR
        );

        this.startDate = startDate;
    }

    @Override
    public Map<String, Object> getProblemDetailProperties() {
        Map<String, Object> properties = new HashMap<>();
        properties.put("startDate", startDate);

        return properties;
    }
}
