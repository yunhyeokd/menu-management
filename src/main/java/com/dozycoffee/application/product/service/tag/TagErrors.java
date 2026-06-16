package com.dozycoffee.application.product.service.tag;

public enum TagErrors {

    UNKNOWN_ERROR(1, "Unknown error"),
    INVALID_TAG_ERROR(2, "Invalid tag"),
    NOT_FOUND_ERROR(3, "Tag not found"),
    DUPLICATE_NAME_ERROR(4, "Duplicate tag name"),
    ;

    public final int errorCode;
    public final String message;

    TagErrors(int errorCode, String message) {
        this.errorCode = errorCode;
        this.message = message;
    }

}
