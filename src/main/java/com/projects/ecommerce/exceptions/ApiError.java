package com.projects.ecommerce.exceptions;

import lombok.Data;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
@Data
public class ApiError {

    private LocalDateTime timeStamp;
    private String message;
    private String cause;
    private HttpStatus statusCode;

    public ApiError(String err, HttpStatus status) {
        this.message = err;
        this.statusCode = status;
        this.timeStamp = LocalDateTime.now();
    }

    public ApiError(String err, String cause, HttpStatus status) {
        this.message = err;
        this.cause = cause;
        this.statusCode = status;
        this.timeStamp = LocalDateTime.now();
    }
}
