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
        type = "booking-past-end-date",
        title = "The end date cannot be in the past.",
        extensions = {
                @Extension(name = "endDate", type = LocalDateTime.class),
        }
)
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
