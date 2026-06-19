package com.dozycoffee.application.branch.service;

public enum BranchErrors {

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

    public final int errorCode;
    public final String message;

    BranchErrors(int errorCode, String message) {
        this.errorCode = errorCode;
        this.message = message;
    }

}
