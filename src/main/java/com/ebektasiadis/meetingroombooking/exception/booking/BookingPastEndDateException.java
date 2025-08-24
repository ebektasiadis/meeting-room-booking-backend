package com.ebektasiadis.meetingroombooking.exception.booking;

import com.ebektasiadis.meetingroombooking.exception.common.AbstractApiException;
import com.ebektasiadis.meetingroombooking.exception.common.ResponseProblemDetail;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Getter
@ResponseStatus(HttpStatus.BAD_REQUEST)
@ResponseProblemDetail(type = "booking-past-end-date", title = "The end date cannot be in the past.")
public class BookingPastEndDateException extends AbstractApiException {
    private final LocalDateTime endDate;

    public BookingPastEndDateException(LocalDateTime endDate) {
        super(
                String.format("The selected end date (%s) is in the past.", endDate)
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
