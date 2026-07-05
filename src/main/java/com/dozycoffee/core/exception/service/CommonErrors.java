package com.dozycoffee.core.exception.service;

public enum CommonErrors implements ServiceError {

    REQUEST_VALIDATION_FAILED(1, "Request Validation Failed"),
    INTERNAL_SERVER_ERROR(2, "Internal Server Error");

    private final int errorCode;
    private final String message;

    CommonErrors(int errorCode, String message) {
        this.errorCode = errorCode;
        this.message = message;
    }

    @Override public int getErrorCode() { return errorCode; }
    @Override public String getMessage() { return message; }
}
