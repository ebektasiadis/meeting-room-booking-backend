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
        type = "booking-invalid-date",
        title = "End time should be before start time.",
        extensions = {
                @Extension(name = "startTime", type = LocalDateTime.class),
                @Extension(name = "endTime", type = LocalDateTime.class),
        }
)
public class BookingInvalidDateException extends AbstractApiException {
    private final LocalDateTime startTime;
    private final LocalDateTime endTime;

    public BookingInvalidDateException(LocalDateTime startTime, LocalDateTime endTime) {
        super(
                String.format("%s is after your start time, which is %s.", endTime, startTime)
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
