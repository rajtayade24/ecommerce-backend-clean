package com.projects.ecommerce.error;

import io.jsonwebtoken.JwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.nio.file.AccessDeniedException;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    // Username Not Found
    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ApiError> handleUsernameNotFoundException(UsernameNotFoundException ex) {
        ApiError apiError = new ApiError("username not found with username: " + ex.getMessage(), HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(apiError, apiError.getStatusCode());
    }

    // AuthenticationException → 401 UNAUTHORIZED
    // (Invalid password, bad credentials, JWT expired inside AuthenticationManager, etc.)
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiError> handleAuthenticationException(AuthenticationException ex) {
        ApiError apiError = new ApiError(
                "Authentication failed: " + ex.getMessage(),
                HttpStatus.UNAUTHORIZED
        );
        return new ResponseEntity<>(apiError, apiError.getStatusCode());
    }

    // JWT Token Error  → 401 UNAUTHORIZED
    // (Invalid token, malformed token, expired token)
    @ExceptionHandler(JwtException.class)
    public ResponseEntity<ApiError> handleJwtException(JwtException ex) {
        ApiError apiError = new ApiError(
                "Invalid or expired JWT token: " + ex.getMessage(),
                HttpStatus.UNAUTHORIZED
        );
        return new ResponseEntity<>(apiError, apiError.getStatusCode());
    }

    // Access Denied       → 403 FORBIDDEN
    // (User logged in but doesn't have permission)
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiError> handleAccessDeniedException(AccessDeniedException ex) {
        ApiError apiError = new ApiError(
                "Access Denied: You don't have permission to perform this action.",
                HttpStatus.FORBIDDEN
        );
        return new ResponseEntity<>(apiError, apiError.getStatusCode());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiError> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        ApiError apiError = new ApiError(
                "Invalid request body: " + ex.getMostSpecificCause().getMessage(),
                HttpStatus.BAD_REQUEST
        );
        return new ResponseEntity<>(apiError, apiError.getStatusCode());
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<String> handleIllegalState(IllegalStateException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGeneralException(Exception ex) {

        log.error("Exception occurred:", ex);

        String cause = ex.getCause() != null ? ex.getCause().getMessage() : "No cause";

        ApiError apiError = new ApiError(
                ex.getMessage(),
                cause,
                HttpStatus.INTERNAL_SERVER_ERROR
        );

        return new ResponseEntity<>(apiError, apiError.getStatusCode());
    }


    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<String> handleMaxSize(MaxUploadSizeExceededException ex) {
        return ResponseEntity.badRequest().body("File too large. Max allowed size is 10MB.");
    }

}
