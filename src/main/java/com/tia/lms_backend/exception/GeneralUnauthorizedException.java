package com.tia.lms_backend.exception;

public class GeneralUnauthorizedException extends RuntimeException {
    public GeneralUnauthorizedException(String message) {
        super(message);
    }
}
