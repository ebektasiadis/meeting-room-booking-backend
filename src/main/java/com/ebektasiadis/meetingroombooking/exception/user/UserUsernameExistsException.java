package com.ebektasiadis.meetingroombooking.exception.user;

import com.ebektasiadis.meetingroombooking.exception.common.AbstractApiException;
import com.ebektasiadis.meetingroombooking.exception.common.ResponseProblemDetail;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.HashMap;
import java.util.Map;

@Getter
@ResponseStatus(HttpStatus.CONFLICT)
@ResponseProblemDetail(type = "user-username-exists", title = "Username is already in use.")
public class UserUsernameExistsException extends AbstractApiException {
    private final String userUsername;

    public UserUsernameExistsException(String userUsername) {
        super(
                String.format("User with the username %s already exists.", userUsername)
        );
        this.userUsername = userUsername;
    }

    @Override
    public Map<String, Object> getProblemDetailProperties() {
        Map<String, Object> properties = new HashMap<>();
        properties.put("userUsername", userUsername);

        return properties;
    }
}
