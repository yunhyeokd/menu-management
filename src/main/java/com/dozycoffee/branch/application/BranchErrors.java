package com.dozycoffee.branch.application;

import com.dozycoffee.core.exception.service.ServiceError;

public enum BranchErrors implements ServiceError {

    UNKNOWN_ERROR(1, "Unknown error"),
    INVALID_BRANCH_ERROR(2, "Invalid branch"),
    DUPLICATE_NAME_ERROR(3, "Duplicate branch name"),
    BRANCH_NOT_FOUND_ERROR(4, "Branch not found"),
    PRODUCT_NOT_FOUND_ERROR(5, "Product not found"),
    SALES_OVERRIDE_NOT_FOUND_ERROR(6, "Sales override not found"),
    INVALID_PROFILE_ERROR(7, "Invalid branch profile"),
    ALREADY_DELETED_ERROR(8, "Branch is already deleted"),
    PRODUCT_NOT_ACTIVE_ERROR(9, "Product is not active"),
    ;

    private final int errorCode;
    private final String message;

    BranchErrors(int errorCode, String message) {
        this.errorCode = errorCode;
        this.message = message;
    }

    @Override public int getErrorCode() { return errorCode; }
    @Override public String getMessage() { return message; }
}
