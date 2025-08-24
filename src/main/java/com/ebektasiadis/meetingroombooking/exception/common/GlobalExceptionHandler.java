package com.ebektasiadis.meetingroombooking.exception.common;

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
        problemDetail.setType(URI.create("/problems/validation-error"));
        problemDetail.setTitle("Some fields are missing or are invalid.");
        problemDetail.setDetail(String.format("There are %d field(s) that are missing or are invalid.", errors.size()));
        problemDetail.setProperty("extensions", Map.of("fields", errors));

        return ResponseEntity.status(problemDetail.getStatus()).body(problemDetail);
    }

    @ExceptionHandler(AbstractApiException.class)
    public ResponseEntity<ProblemDetail> handleAbstractApiException(AbstractApiException ex) throws NoSuchMethodException {
        ResponseStatus responseStatusAnnotation = AnnotationUtils.findAnnotation(ex.getClass(), ResponseStatus.class);
        ResponseProblemDetail responseProblemDetailAnnotation = AnnotationUtils.findAnnotation(ex.getClass(), ResponseProblemDetail.class);

        HttpStatus status = (responseStatusAnnotation != null) ? responseStatusAnnotation.value() : (HttpStatus) ResponseStatus.class.getMethod("value").getDefaultValue();
        String title = (responseProblemDetailAnnotation != null) ? responseProblemDetailAnnotation.title() : ResponseProblemDetail.class.getMethod("title").getDefaultValue().toString();
        URI type = URI.create("/problems/" + ((responseProblemDetailAnnotation != null) ? responseProblemDetailAnnotation.type() : ResponseProblemDetail.class.getMethod("type").getDefaultValue().toString()));

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(status, ex.getMessage());
        problemDetail.setTitle(title);
        problemDetail.setType(type);
        problemDetail.setProperty("extensions", ex.getProblemDetailProperties());

        return ResponseEntity.status(problemDetail.getStatus()).body(problemDetail);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> handleException(Exception ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        problemDetail.setType(URI.create("/problems/internal-error"));
        return ResponseEntity.status(problemDetail.getStatus()).body(problemDetail);
    }
}
