package com.projects.ecommerce.error;

import lombok.Data;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
@Data
public class ApiError {

    private LocalDateTime timeStamp;
    private String error;
    private String cause;
    private HttpStatus statusCode;

    public ApiError(String err, HttpStatus status) {
        this.error = err;
        this.statusCode = status;
        this.timeStamp = LocalDateTime.now();
    }

    public ApiError(String err, String cause, HttpStatus status) {
        this.error = err;
        this.cause = cause;
        this.statusCode = status;
        this.timeStamp = LocalDateTime.now();
    }
}
