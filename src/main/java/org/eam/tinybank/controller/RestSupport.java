package org.eam.tinybank.controller;

import lombok.NonNull;
import org.eam.tinybank.api.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * Provides useful methods to implementing classes. Assumed to be used with REST controllers.
 */
interface RestSupport {

    /**
     * Simple {@link ResponseEntity} wrapper, evaluating HTTP code from a given response.
     */
    default ResponseEntity<ApiResponse> responseFrom(@NonNull ApiResponse response) {
        return ResponseEntity.status(statusFrom(response)).body(response);
    }

    /**
     * Evaluates HTTP status code based on API response created in services.
     */
    private HttpStatus statusFrom(@NonNull ApiResponse response) {
        return response.failed() ? HttpStatus.BAD_REQUEST : HttpStatus.OK;
    }

}
