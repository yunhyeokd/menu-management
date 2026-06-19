package com.dozycoffee.application.branch.service;

public enum BranchErrors {

    UNKNOWN_ERROR(1, "Unknown error"),
    INVALID_BRANCH_ERROR(2, "Invalid branch"),
    DUPLICATE_NAME_ERROR(3, "Duplicate branch name"),
    NOT_FOUND_ERROR(4, "Branch not found"),
    ;

    public final int errorCode;
    public final String message;

    BranchErrors(int errorCode, String message) {
        this.errorCode = errorCode;
        this.message = message;
    }

}
