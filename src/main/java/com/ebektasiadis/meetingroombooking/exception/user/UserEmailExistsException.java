package com.ebektasiadis.meetingroombooking.exception.user;

import com.ebektasiadis.meetingroombooking.constants.ProblemTypes;
import com.ebektasiadis.meetingroombooking.exception.common.AbstractApiException;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.HashMap;
import java.util.Map;

@Getter
@ResponseStatus(value = HttpStatus.CONFLICT)
public class UserEmailExistsException extends AbstractApiException {
    private final String userEmail;

    public UserEmailExistsException(String userEmail) {
        super(
                String.format("User with the email %s already exists.", userEmail),
                "Email is already in use.",
                ProblemTypes.USER_EMAIL_EXISTS_ERROR
        );

        this.userEmail = userEmail;
    }

    @Override
    public Map<String, Object> getProblemDetailProperties() {
        Map<String, Object> properties = new HashMap<>();
        properties.put("userEmail", userEmail);

        return properties;
    }
}
