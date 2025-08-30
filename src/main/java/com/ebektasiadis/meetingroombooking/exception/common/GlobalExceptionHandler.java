package com.ebektasiadis.meetingroombooking.exception.common;

import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
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
        ResponseProblemDetail responseProblemDetailAnnotation = AnnotationUtils.findAnnotation(ex.getClass(), ResponseProblemDetail.class);

        HttpStatus status;
        String title;
        String type;

        if (responseProblemDetailAnnotation == null) {
            status = (HttpStatus) ResponseProblemDetail.class.getMethod("status").getDefaultValue();
            title = ResponseProblemDetail.class.getMethod("title").getDefaultValue().toString();
            type = ResponseProblemDetail.class.getMethod("type").getDefaultValue().toString();
        } else {
            status = responseProblemDetailAnnotation.status();
            title = responseProblemDetailAnnotation.title();
            type = responseProblemDetailAnnotation.type();
        }

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(status, ex.getMessage());
        problemDetail.setTitle(title);
        problemDetail.setType(URI.create(String.format("/problems/%s", type)));
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
