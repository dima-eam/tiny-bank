package org.eam.tinybank.controller;

import org.eam.tinybank.api.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * Provides useful methods to implementing classes.
 */
interface RestSupport {

    default ResponseEntity<ApiResponse> responseFrom(ApiResponse response) {
        return ResponseEntity.status(statusFrom(response)).body(response);
    }

    /**
     * Evaluates HTTP status code based on API response created in services.
     */
    private HttpStatus statusFrom(ApiResponse response) {
        return response.failed() ? HttpStatus.BAD_REQUEST : HttpStatus.OK;
    }

}
