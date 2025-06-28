package com.ebektasiadis.meetingroombooking.exception.user;

import com.ebektasiadis.meetingroombooking.constants.ProblemTypes;
import com.ebektasiadis.meetingroombooking.exception.common.AbstractApiException;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.HashMap;
import java.util.Map;

@Getter
@ResponseStatus(HttpStatus.NOT_FOUND)
public class UserNotFoundException extends AbstractApiException {
    private final Long userId;

    public UserNotFoundException(Long userId) {
        super(
                String.format("User with id %d couldn't be found.", userId),
                "User not found.",
                ProblemTypes.USER_NOT_FOUND_ERROR
        );
        this.userId = userId;
    }

    @Override
    public Map<String, Object> getProblemDetailProperties() {
        Map<String, Object> properties = new HashMap<>();
        properties.put("userId", this.userId);

        return properties;
    }
}
