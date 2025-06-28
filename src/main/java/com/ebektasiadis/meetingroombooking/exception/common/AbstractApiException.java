package com.ebektasiadis.meetingroombooking.exception.common;

import lombok.Getter;

import java.net.URI;
import java.util.Map;

@Getter
public abstract class AbstractApiException extends RuntimeException {
    private final String title;
    private final URI type;

    public AbstractApiException(String message, String title, String type) {
        super(message);
        this.title = title;
        this.type = URI.create("/problems/" + type);
    }

    public abstract Map<String, Object> getProblemDetailProperties();
}
