package com.ebektasiadis.meetingroombooking.exception.user;

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
        type = "user-not-found",
        title = "User not found.",
        extensions = {
                @Extension(name = "userId", type = Long.class),
        }
)
public class UserNotFoundException extends AbstractApiException {
    private final Long userId;

    public UserNotFoundException(Long userId) {
        super(
                String.format("User with id %d couldn't be found.", userId)
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
