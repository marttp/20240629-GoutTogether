package dev.tpcoder.goutbackend.common;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import dev.tpcoder.goutbackend.common.exception.BookingExistsException;
import dev.tpcoder.goutbackend.common.exception.EntityNotFoundException;
import dev.tpcoder.goutbackend.common.exception.RefreshTokenExpiredException;
import dev.tpcoder.goutbackend.common.exception.UserIdMismatchException;

@RestControllerAdvice
public class ResponseAdviceHandler extends ResponseEntityExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(ResponseAdviceHandler.class);

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException e,
            HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        var text = "Invalid arguments request";
        ProblemDetail detail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, text);
        Map<String, Object> properties = new HashMap<>();
        var invalidArgumentList = e.getBindingResult().getFieldErrors();
        for (var oe : invalidArgumentList) {
            properties.put(oe.getField(), oe.getDefaultMessage());
        }
        detail.setProperty("arguments", properties);
        return ResponseEntity.badRequest().body(detail);
    }

    @ExceptionHandler(UserIdMismatchException.class)
    protected ResponseEntity<Object> userIdMismatchExceptionHandling(UserIdMismatchException e) {
        ProblemDetail detail = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST,
                e.getMessage());
        logger.info("UserId mismatch request: {}", detail);
        return ResponseEntity.badRequest().body(detail);
    }

    @ExceptionHandler(BookingExistsException.class)
    protected ResponseEntity<Object> bookingExistsExceptionHandling(BookingExistsException e) {
        ProblemDetail detail = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST,
                e.getMessage());
        logger.info("Booking exists: {}", detail);
        return ResponseEntity.badRequest().body(detail);
    }

    @ExceptionHandler(RefreshTokenExpiredException.class)
    protected ResponseEntity<Object> globalExceptionHandling(RefreshTokenExpiredException e) {
        ProblemDetail detail = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST,
                e.getMessage());
        logger.info("Refresh token expired: {}", detail);
        return ResponseEntity.badRequest().body(detail);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    protected ResponseEntity<Object> globalExceptionHandling(EntityNotFoundException e) {
        ProblemDetail detail = ProblemDetail.forStatusAndDetail(
                HttpStatus.NOT_FOUND,
                e.getMessage());
        logger.info("Entity not found: {}", detail);
        return ResponseEntity.notFound().build();
    }

    // Global handling
    @ExceptionHandler(Exception.class)
    protected ResponseEntity<Object> globalExceptionHandling(Exception e) {
        ProblemDetail detail = ProblemDetail.forStatusAndDetail(
                HttpStatus.INTERNAL_SERVER_ERROR,
                e.getMessage());
        logger.error(e.getMessage());
        e.printStackTrace();
        return ResponseEntity.internalServerError()
                .body(detail);
    }
}
