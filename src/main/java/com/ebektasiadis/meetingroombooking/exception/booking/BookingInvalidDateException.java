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
public class BookingInvalidDateException extends AbstractApiException {
    private final LocalDateTime startTime;
    private final LocalDateTime endTime;

    public BookingInvalidDateException(LocalDateTime startTime, LocalDateTime endTime) {
        super(
                String.format("%s is after your start time, which is %s.", endTime, startTime),
                "End time should be before start time.",
                ProblemTypes.BOOKING_INVALID_DATE_ERROR
        );

        this.startTime = startTime;
        this.endTime = endTime;
    }


    @Override
    public Map<String, Object> getProblemDetailProperties() {
        Map<String, Object> properties = new HashMap<>();
        properties.put("startTime", startTime);
        properties.put("endTime", endTime);

        return properties;
    }
}
