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
public class BookingPastEndDateException extends AbstractApiException {
    private final LocalDateTime endDate;

    public BookingPastEndDateException(LocalDateTime endDate) {
        super(
                String.format("The selected end date (%s) is in the past.", endDate),
                "The end date cannot be in the past.",
                ProblemTypes.BOOKING_PAST_END_DATE_ERROR
        );

        this.endDate = endDate;
    }

    @Override
    public Map<String, Object> getProblemDetailProperties() {
        Map<String, Object> properties = new HashMap<>();
        properties.put("endDate", endDate);

        return properties;
    }
}
