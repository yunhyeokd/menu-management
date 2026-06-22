package com.dozycoffee.application.product.service.option;

public enum OptionErrors {

    UNKNOWN_ERROR(1, "Unknown Error"),
    INVALID_OPTION_ERROR(3, "Invalid Option"),
    OPTION_GROUP_NOT_FOUND_ERROR(4, "Option Group Not Found"),
    OPTION_ITEM_NOT_FOUND_ERROR(5, "Option Item Not Found"),
    EMPTY_OPTION_GROUP_ERROR(6, "No Option Group Items"),
    LINKED_PRODUCT_EXISTS_ERROR(7, "Linked Products exists");

    public final int errorCode;
    public final String message;

    OptionErrors(Integer code, String message) {
        this.errorCode = code;
        this.message = message;
    }

}
