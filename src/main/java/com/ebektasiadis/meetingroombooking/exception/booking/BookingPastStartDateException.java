package com.ebektasiadis.meetingroombooking.exception.booking;

import com.ebektasiadis.meetingroombooking.exception.common.AbstractApiException;
import com.ebektasiadis.meetingroombooking.exception.common.Extension;
import com.ebektasiadis.meetingroombooking.exception.common.ResponseProblemDetail;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Getter
@ResponseProblemDetail(
        status = HttpStatus.BAD_REQUEST,
        type = "booking-past-start-date",
        title = "The start date cannot be in the past.",
        extensions = {
                @Extension(name = "startDate", type = LocalDateTime.class),
        }
)
public class BookingPastStartDateException extends AbstractApiException {
    private final LocalDateTime startDate;

    public BookingPastStartDateException(LocalDateTime startDate) {
        super(
                String.format("The selected start date (%s) is in the past.", startDate)
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
