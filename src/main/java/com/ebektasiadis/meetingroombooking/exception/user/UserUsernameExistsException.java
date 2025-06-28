package com.ebektasiadis.meetingroombooking.exception.user;

import com.ebektasiadis.meetingroombooking.constants.ProblemTypes;
import com.ebektasiadis.meetingroombooking.exception.common.AbstractApiException;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.HashMap;
import java.util.Map;

@Getter
@ResponseStatus(HttpStatus.CONFLICT)
public class UserUsernameExistsException extends AbstractApiException {
    private final String userUsername;

    public UserUsernameExistsException(String userUsername) {
        super(
                String.format("User with the username %s already exists.", userUsername),
                "Username is already in use.",
                ProblemTypes.USER_USERNAME_EXISTS_ERROR
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
