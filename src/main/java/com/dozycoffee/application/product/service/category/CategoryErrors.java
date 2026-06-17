package com.dozycoffee.application.product.service.category;

public enum CategoryErrors {

    UNKNOWN_ERROR(1, "Unknown error"),
    INVALID_CATEGORY_ERROR(2, "Invalid category"),
    DUPLICATE_NAME_ERROR(3, "Duplicate category name"),
    NOT_FOUND_ERROR(4, "Category not found"),
    ;

    public final int errorCode;
    public final String message;

    CategoryErrors(int errorCode, String message) {
        this.errorCode = errorCode;
        this.message = message;
    }

}
