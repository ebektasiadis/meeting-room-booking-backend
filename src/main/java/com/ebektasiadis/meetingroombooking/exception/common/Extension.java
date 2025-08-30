package com.ebektasiadis.meetingroombooking.exception.common;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({})
@Retention(RetentionPolicy.RUNTIME)
public @interface Extension {
    String name();

    Class<?> type();

}
