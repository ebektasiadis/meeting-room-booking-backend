package com.ebektasiadis.meetingroombooking.documentation;

import com.ebektasiadis.meetingroombooking.exception.common.AbstractApiException;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface DocumentedExceptions {
    Class<? extends AbstractApiException>[] value() default {};
}
