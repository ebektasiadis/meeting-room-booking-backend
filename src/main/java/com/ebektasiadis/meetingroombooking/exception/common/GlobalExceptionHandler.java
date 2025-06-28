package com.ebektasiadis.meetingroombooking.exception.common;

import com.ebektasiadis.meetingroombooking.constants.ProblemTypes;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;


@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetail> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getFieldErrors().forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));

        ProblemDetail problemDetail = ProblemDetail.forStatus(ex.getStatusCode());
        problemDetail.setType(URI.create(String.format("/problems/%s", ProblemTypes.VALIDATION_ERROR)));
        problemDetail.setTitle("Some fields are missing or are invalid.");
        problemDetail.setDetail(String.format("There are %d field(s) that are missing or are invalid.", errors.size()));
        problemDetail.setProperty("extensions", Map.of("fields", errors));

        return ResponseEntity.status(problemDetail.getStatus()).body(problemDetail);
    }

    @ExceptionHandler(AbstractApiException.class)
    public ResponseEntity<ProblemDetail> handleAbstractApiException(AbstractApiException ex) {
        ResponseStatus responseStatusAnnotation = AnnotationUtils.findAnnotation(ex.getClass(), ResponseStatus.class);
        HttpStatus status = (responseStatusAnnotation != null) ? responseStatusAnnotation.value() : HttpStatus.INTERNAL_SERVER_ERROR;

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(status, ex.getMessage());
        problemDetail.setTitle(ex.getTitle());
        problemDetail.setType(ex.getType());
        problemDetail.setProperty("extensions", ex.getProblemDetailProperties());

        return ResponseEntity.status(problemDetail.getStatus()).body(problemDetail);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> handleException(Exception ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        problemDetail.setType(URI.create(String.format("/problems/%s", ProblemTypes.INTERNAL_ERROR)));
        return ResponseEntity.status(problemDetail.getStatus()).body(problemDetail);
    }
}
