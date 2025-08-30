package com.ebektasiadis.meetingroombooking.exception.common;

import org.springframework.http.HttpStatus;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ResponseProblemDetail {
    String type() default "unknown-problem-detail-type";

    String title() default "Unknown problem detail title.";

    HttpStatus status() default HttpStatus.BAD_REQUEST;

    Extension[] extensions() default {};
}
