package com.dozycoffee.application.admin.service;

public enum AdminErrors {

    UNKNOWN_ERROR(1, "Unknown Error"),
    INVALID_ADMIN_ERROR(2, "Invalid Admin Error"),
    ADMIN_NOT_FOUND(3, "Admin Not Found"),
    DUPLICATE_ACCOUNT_ERROR(4, "Duplicate Account"),
    UNABLE_APPROVAL_ERROR(5, "Account is not a subject to approval"),
    AUTHENTICATION_FAILED_ERROR(6, "Authentication Failed"),;

    public final Integer errorCode;
    public final String message;
    AdminErrors(Integer errorCode, String message) {
        this.errorCode = errorCode;
        this.message = message;
    }



}
