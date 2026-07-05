package com.dozycoffee.admin.application;

import com.dozycoffee.core.exception.service.ServiceError;

public enum AdminErrors implements ServiceError {

    UNKNOWN_ERROR(1, "Unknown Error"),
    INVALID_ADMIN_ERROR(2, "Invalid Admin Error"),
    ADMIN_NOT_FOUND(3, "Admin Not Found"),
    DUPLICATE_ACCOUNT_ERROR(4, "Duplicate Account"),
    UNABLE_APPROVAL_ERROR(5, "Account is not a subject to approval"),
    AUTHENTICATION_FAILED_ERROR(6, "Authentication Failed"),
    DUPLICATE_EMPLOYEE_NO_ERROR(7, "Duplicate Employee Number"),
    ALREADY_DELETED_ERROR(8, "Admin Already Deleted"),;

    private final int errorCode;
    private final String message;

    AdminErrors(int errorCode, String message) {
        this.errorCode = errorCode;
        this.message = message;
    }

    @Override public int getErrorCode() { return errorCode; }
    @Override public String getMessage() { return message; }
}
