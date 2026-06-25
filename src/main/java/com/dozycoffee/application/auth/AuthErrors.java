package com.dozycoffee.application.auth;

public enum AuthErrors {

    UNKNOWN_ERROR(1, "Unknown error"),
    UNAUTHENTICATED(2, "Unauthenticated"),
    UNAUTHORIZED(3, "Unauthorized"),
    INVALID_CREDENTIAL(4, "Invalid credential"),
    SESSION_EXPIRED(5, "Session expired");

    public final int errorCode;
    public final String message;

    AuthErrors(int errorCode, String message) {
        this.errorCode = errorCode;
        this.message = message;
    }

}
