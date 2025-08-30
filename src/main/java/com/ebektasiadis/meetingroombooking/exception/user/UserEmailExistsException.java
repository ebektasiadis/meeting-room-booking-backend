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
        status = HttpStatus.CONFLICT,
        type = "user-email-exists",
        title = "Email is already in use.",
        extensions = {
                @Extension(name = "userEmail", type = String.class),
        }
)
public class UserEmailExistsException extends AbstractApiException {
    private final String userEmail;

    public UserEmailExistsException(String userEmail) {
        super(String.format("User with the email %s already exists.", userEmail));

        this.userEmail = userEmail;
    }

    @Override
    public Map<String, Object> getProblemDetailProperties() {
        Map<String, Object> properties = new HashMap<>();
        properties.put("userEmail", userEmail);

        return properties;
    }
}
