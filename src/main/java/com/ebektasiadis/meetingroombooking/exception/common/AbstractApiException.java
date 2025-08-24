package com.ebektasiadis.meetingroombooking.exception.common;

import lombok.Getter;

import java.util.Map;

@Getter
public abstract class AbstractApiException extends RuntimeException {

    public AbstractApiException(String message) {
        super(message);
    }

    public abstract Map<String, Object> getProblemDetailProperties();
}
