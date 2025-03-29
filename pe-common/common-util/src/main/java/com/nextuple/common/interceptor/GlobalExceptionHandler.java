package com.nextuple.common.interceptor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.firewall.RequestRejectedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(RequestRejectedException.class)
    public ResponseEntity<String> handleRequestRejectedException(RequestRejectedException ex) {
        logger.error("RequestRejectedException occurred: ", ex);
        return new ResponseEntity<>("Request was rejected: " + ex.getMessage(), HttpStatus.BAD_REQUEST);
    }
}
