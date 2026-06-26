package com.dozycoffee.auth.application;

import com.dozycoffee.core.application.ServiceError;

public enum AuthErrors implements ServiceError {

    UNKNOWN_ERROR(1, "Unknown error"),
    UNAUTHENTICATED(2, "Unauthenticated"),
    UNAUTHORIZED(3, "Unauthorized"),
    INVALID_CREDENTIAL(4, "Invalid credential"),
    SESSION_EXPIRED(5, "Session expired");

    private final int errorCode;
    private final String message;

    AuthErrors(int errorCode, String message) {
        this.errorCode = errorCode;
        this.message = message;
    }

    @Override public int getErrorCode() { return errorCode; }
    @Override public String getMessage() { return message; }
}
