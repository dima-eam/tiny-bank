package org.eam.tinybank.controller;

import org.eam.tinybank.api.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * Processes all unexpected exceptions to produce same {@link ApiResponse} as for regular calls.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = RuntimeException.class)
    public ResponseEntity<ApiResponse> exception(RuntimeException exception) {
        return new ResponseEntity<>(ApiResponse.error(exception.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }

}